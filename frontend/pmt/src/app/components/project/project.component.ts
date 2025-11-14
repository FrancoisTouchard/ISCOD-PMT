import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { AuthService } from '../../services/auth.service';
import { ContributorService } from '../../services/contributor.service';
import { ToastService } from '../../services/toast.service';
import { LocalTask } from '../../models/task.model';
import { TaskService } from '../../services/task.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandlerService } from '../../services/errorHandler.service';
import { ProjectTasksComponent } from './project-tasks/project-tasks.component';
import {
  ProjectMembersComponent,
  ContributorRoleUpdateData,
} from './project-members/project-members.component';
import { getRoleLabel } from '../../utils/labels.utils';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Role } from '../../models/role.enum';
import { getCurrentUserRole } from '../../utils/role.utils';

interface ContributorAddData {
  email: string;
  role: Role;
}

@Component({
  selector: 'app-project',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ProjectTasksComponent,
    ProjectMembersComponent,
  ],
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss'],
})
export class ProjectComponent implements OnInit, OnDestroy {
  @ViewChild('tasksComponent') tasksComponent?: ProjectTasksComponent;

  addContributorForm: FormGroup;
  isContributorSubmitted = false;
  activeTab: string = 'tasks';
  private destroy$ = new Subject<void>();
  getCurrentUserRole = getCurrentUserRole;
  getRoleLabel = getRoleLabel;
  loading = false;
  project: Project | null = null;
  projectId = '';
  Role = Role;
  isMemberModalOpen = false;
  userId: string | null = '';
  currentUserRole: Role | null = null;

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
    this.userId = localStorage.getItem('userId');
    // Récupérer le projet depuis le state de navigation
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || history.state;

    if (state && state['project']) {
      this.project = state['project'];
      if (this.userId)
        this.currentUserRole = getCurrentUserRole(this.project, this.userId);
      this.loadTasksOnly();
    } else if (this.projectId) {
      // Fallback : charger le projet depuis l'API (ex: accès direct via URL)
      this.loadProjectAndTasks();
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

  goToHomePage() {
    this.router.navigate(['/home']);
  }

  logOut() {
    this.authService.logout();
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  closeMemberModal() {
    this.addContributorForm.reset({
      contributorData: {
        email: null,
        role: Role.OBSERVATEUR,
      },
    });
    this.isContributorSubmitted = false;
    this.isMemberModalOpen = false;
  }

  showAddContributorBlock() {
    this.isMemberModalOpen = true;
  }

  openTaskModalFromParent(): void {
    this.activeTab = 'tasks';
    setTimeout(() => {
      this.tasksComponent?.openModal();
    });
  }

  loadTasksOnly(): void {
    this.loading = true;

    this.taskService
      .getTasksByProjectId(this.projectId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (tasks) => {
          this.taskService.tasks = tasks;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.toastService.showToast(
            'Erreur lors du chargement des tâches',
            'error'
          );
          this.loading = false;
        },
      });
  }

  loadProjectAndTasks(): void {
    this.loading = true;

    this.projectService
      .getProjectById(this.projectId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (project) => {
          this.project = project;
          this.loadTasksOnly();
        },
        error: (err) => {
          console.error(err);
          this.toastService.showToast(
            'Erreur lors du chargement du projet',
            'error'
          );
          this.loading = false;
        },
      });
  }

  // Gestionnaires d'événements pour les tâches

  onTaskAdded(newTask: LocalTask): void {
    this.taskService
      .createTask(this.projectId, newTask)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (createdTask) => {
          this.loadTasksOnly();
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
    // récupérer le user id pour préciser l'auteur de la modification dans l'historique
    this.userId = localStorage.getItem('userId') || '';
    this.taskService
      .updateTask(this.projectId, this.userId, event.taskId, event.updatedTask)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedTask) => {
          this.toastService.showToast(
            `Tâche "${updatedTask.name}" mise à jour avec succès !`,
            'success'
          );
          this.loadTasksOnly();
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

  onTaskDeleted(taskId: string): void {
    this.taskService
      .deleteTaskById(taskId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadTasksOnly();
          this.toastService.showToast(
            `Tâche supprimée avec succès !`,
            'success'
          );
        },
        error: (err) => {
          console.error('Erreur lors de la suppression de la tâche:', err);
          this.toastService.showToast(
            'Erreur lors de la suppression de la tâche',
            'error'
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
          this.loadProjectAndTasks();
          this.activeTab = 'members';
          this.toastService.showToast(
            `Contributeur "${addedContributor.userEmail}" ajouté avec le rôle ${
              this.getRoleLabel(addedContributor.role).label
            } !`,
            'success'
          );
          this.closeMemberModal();
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
          this.loadProjectAndTasks();
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
          this.loadProjectAndTasks();
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
