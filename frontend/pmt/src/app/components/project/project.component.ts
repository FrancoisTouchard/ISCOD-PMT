import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, Subject, takeUntil } from 'rxjs';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { AuthService } from '../../services/auth.service';
import { ContributorService } from '../../services/contributor.service';
import { ToastService } from '../../services/toast.service';
import { LocalTask } from '../../models/task.model';
import { TaskService } from '../../services/task.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandlerService } from '../../services/errorHandler.service';
import { ProjectTasksComponent } from '../project-tasks/project-tasks.component';
import {
  ProjectMembersComponent,
  ContributorRoleUpdateData,
} from '../project-members/project-members.component';
import { getRoleString } from '../../utils/labels';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Role } from '../../models/role.enum';

interface ContributorAddData {
  email: string;
  role: Role;
}

@Component({
  selector: 'app-project',
  standalone: true,
  imports: [
    CommonModule,
    ProjectTasksComponent,
    ProjectMembersComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss'],
})
export class ProjectComponent implements OnInit, OnDestroy {
  @ViewChild('tasksComp') tasksComp?: ProjectTasksComponent;

  addContributorForm: FormGroup;
  isContributorSubmitted = false;
  activeTab: string = 'tasks';
  private destroy$ = new Subject<void>();
  getRoleLabel = getRoleString;
  loading = false;
  project: Project | null = null;
  projectId = '';
  Role = Role;
  showTaskForm = false;
  showMemberForm = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private contributorService: ContributorService,
    private errorHandlerService: ErrorHandlerService,
    private projectService: ProjectService,
    private taskService: TaskService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.addContributorForm = new FormGroup({
      contributorData: new FormGroup({
        email: new FormControl(null, [Validators.required, Validators.email]),
        role: new FormControl(Role.OBSERVATEUR, [Validators.required]),
      }),
    });
  }

  ngOnInit(): void {
    this.projectId = this.route.snapshot.paramMap.get('id') || '';
    if (this.projectId) {
      this.loadProject();
    } else {
      this.toastService.showToast(`Projet non trouvé`, 'error');
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get email() {
    return this.addContributorForm.get('contributorData.email') as FormControl;
  }

  get role() {
    return this.addContributorForm.get('contributorData.role') as FormControl;
  }

  get tasks() {
    return this.taskService.tasks;
  }

  hideAddContributorBlock() {
    this.addContributorForm.reset({
      contributorData: {
        email: null,
        role: Role.OBSERVATEUR,
      },
    });
    this.isContributorSubmitted = false;
    this.showMemberForm = false;
  }

  goToHomePage() {
    this.router.navigate(['/home']);
  }

  logOut() {
    this.authService.logout();
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  showAddTaskBlock() {
    this.showTaskForm = true;
  }

  openTaskModalFromParent(): void {
    this.activeTab = 'tasks';
    setTimeout(() => {
      this.tasksComp?.openCreateModal();
    });
  }

  showAddContributorBlock() {
    this.showMemberForm = true;
  }

  loadProject(): void {
    this.loading = true;

    forkJoin({
      project: this.projectService.getProjectById(this.projectId),
      tasks: this.taskService.getTasksByProjectId(this.projectId),
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ({ project, tasks }) => {
          this.project = project;
          this.taskService.tasks = tasks;
          this.loading = false;
          console.log('Projet et tâches chargés', { project, tasks });
        },
        error: (err) => {
          console.error(err);
          this.toastService.showToast(
            'Erreur lors du chargement des données',
            'error'
          );
          this.loading = false;
        },
      });
  }

  // Gestionnaires d'événements pour les tâches

  onTaskAdded(newTask: LocalTask): void {
    console.log('Ajout de tâche:', newTask);

    this.taskService
      .createTask(this.projectId, newTask)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (createdTask) => {
          this.loadProject();
          this.activeTab = 'tasks';
          this.toastService.showToast(
            `Tâche "${createdTask.name}" ajoutée avec succès !`,
            'success'
          );
        },
        error: (err) => {
          console.error('Erreur lors de la création de la tâche :', err);
          this.errorHandlerService.handleError(
            err,
            "Erreur lors de l'ajout de la tâche"
          );
        },
      });
  }

  onTaskUpdated(event: { taskId: string; updatedTask: LocalTask }) {
    this.taskService
      .updateTask(this.projectId, event.taskId, event.updatedTask)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedTask) => {
          this.toastService.showToast(
            `Tâche "${updatedTask.name}" mise à jour avec succès !`,
            'success'
          );
          this.loadProject();
        },
        error: (err) => {
          console.error('Erreur lors de la mise à jour de la tâche :', err);
          this.errorHandlerService.handleError(
            err,
            'Erreur lors de la mise à jour de la tâche'
          );
        },
      });
  }

  // Gestionnaires d'événements pour les contributeurs

  onContributorAdded() {
    this.isContributorSubmitted = true;
    if (this.addContributorForm.invalid) return;

    const formValue = this.addContributorForm.value.contributorData;
    const data: ContributorAddData = {
      email: formValue.email,
      role: formValue.role,
    };

    this.contributorService
      .addContributor(this.projectId, data.email, data.role)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (addedContributor) => {
          this.loadProject();
          this.activeTab = 'members';
          this.toastService.showToast(
            `Contributeur "${
              addedContributor.userEmail
            }" ajouté avec le rôle ${this.getRoleLabel(
              addedContributor.role
            )} !`,
            'success'
          );
          this.hideAddContributorBlock();
        },
        error: (err: HttpErrorResponse) => {
          this.errorHandlerService.handleError(
            err,
            "Erreur lors de l'ajout du contributeur"
          );
        },
      });
  }

  onContributorDeleted(contributorId: string): void {
    this.contributorService
      .deleteContributor(this.projectId, contributorId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadProject();
          this.toastService.showToast(
            `Le contributeur a été retiré du projet`,
            'success'
          );
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du contributeur:', err);
          this.toastService.showToast(
            'Erreur lors de la suppression du contributeur',
            'error'
          );
        },
      });
  }

  onContributorRoleUpdated(data: ContributorRoleUpdateData): void {
    this.loading = true;
    this.contributorService
      .updateContributorRole(this.projectId, data.contributorId, data.newRole)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedContributor) => {
          this.loadProject();
          this.loading = false;
          this.toastService.showToast(
            `${updatedContributor.userName} est maintenant ${updatedContributor.role}`,
            'success'
          );
        },
        error: (err) => {
          this.loading = false;
          console.error('Erreur:', err);
          this.toastService.showToast(
            `Le rôle n'a pas pu être mis à jour`,
            'error'
          );
        },
      });
  }
}
