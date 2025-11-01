import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, Subject, takeUntil } from 'rxjs';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { AuthService } from '../../services/auth.service';
import { Contributor } from '../../models/contributor.model';
import { Role } from '../../models/role.enum';
import { getRoleString } from '../../utils/labels';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { ContributorService } from '../../services/contributor.service';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ToastService } from '../../services/toast.service';
import { Priority } from '../../models/priority.enum';
import { LocalTask, Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandlerService } from '../../services/errorHandler.service';

@Component({
  selector: 'app-project',
  imports: [
    CommonModule,
    NgbDropdownModule,
    ReactiveFormsModule,
    NgClass,
    NgFor,
    NgIf,
  ],
  templateUrl: './project.component.html',
  styleUrl: './project.component.scss',
})
export class ProjectComponent implements OnInit, OnDestroy {
  activeTab: string = 'tasks';
  addContributorForm: FormGroup;
  addTaskForm: FormGroup;
  private destroy$ = new Subject<void>();
  getRoleLabel = getRoleString;
  loading = false;
  isAddContributorBlockVisible = false;
  isContributorSubmitted = false;
  isAddTaskBlockVisible = false;
  isTaskSubmitted = false;
  project: Project | null = null;
  projectId = '';
  Role = Role;
  roles = Object.values(Role);
  Priority = Priority;

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
    this.addTaskForm = new FormGroup({
      taskData: new FormGroup({
        name: new FormControl(null, [Validators.required]),
        description: new FormControl(null),
        dueDate: new FormControl(null, [Validators.required]),
        endDate: new FormControl(null),
        priority: new FormControl(Priority.MEDIUM, [Validators.required]),
        assigneeIds: new FormControl([]),
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

  get taskName() {
    return this.addTaskForm.get('taskData.name') as FormControl;
  }

  get taskDueDate() {
    return this.addTaskForm.get('taskData.dueDate') as FormControl;
  }

  get taskEndDate() {
    return this.addTaskForm.get('taskData.endDate') as FormControl;
  }

  get taskPriority() {
    return this.addTaskForm.get('taskData.priority') as FormControl;
  }

  get taskAssigneeIds() {
    return this.addTaskForm.get('taskData.assigneeIds') as FormControl;
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

  showAddContributorBlock() {
    this.isAddContributorBlockVisible = true;
  }

  hideAddContributorBlock() {
    this.isAddContributorBlockVisible = false;
    this.addContributorForm.reset({
      contributorData: {
        email: null,
        role: Role.ADMINISTRATEUR,
      },
    });
    this.isContributorSubmitted = false;
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
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

  // Méthodes de gestion des contributeurs

  onAddContributorSubmit() {
    this.isContributorSubmitted = true;
    if (this.addContributorForm.invalid) return;

    const email = this.email.value;
    const role = this.role.value;
    console.log('Ajout contributeur:', email, role, this.projectId);

    this.contributorService
      .addContributor(this.projectId, email, role)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (addedContributor) => {
          this.hideAddContributorBlock();
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
        },
        error: (err: HttpErrorResponse) => {
          this.errorHandlerService.handleError(
            err,
            "Erreur lors de l'ajout du contributeur"
          );
        },
      });
  }

  deleteContributor(contributor: Contributor): void {
    if (
      !confirm(
        `Voulez-vous vraiment retirer ${contributor.userName} du projet ?`
      )
    ) {
      return;
    }

    this.contributorService
      .deleteContributor(this.projectId, contributor.id.idUser)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadProject();
          this.toastService.showToast(
            `${contributor.userName} a été retiré du projet`,
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

  updateContributorRole(contributor: Contributor, newRole: Role): void {
    if (contributor.role === newRole) return;

    this.loading = true;
    this.contributorService
      .updateContributorRole(this.projectId, contributor.id.idUser, newRole)
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

  // méthodes de gestion des tâches

  showAddTaskBlock() {
    this.isAddTaskBlockVisible = true;
  }

  hideAddTaskBlock() {
    this.isAddTaskBlockVisible = false;
    this.isTaskSubmitted = false;
    this.addTaskForm.reset({
      taskData: {
        name: null,
        description: null,
        dueDate: null,
        endDate: null,
        priority: Priority.MEDIUM,
        assigneeIds: null,
      },
    });
  }

  onAssigneeToggle(userId: string, checked: boolean) {
    const assignees = this.taskAssigneeIds.value as string[];
    if (checked) {
      this.taskAssigneeIds.setValue([...assignees, userId]);
    } else {
      this.taskAssigneeIds.setValue(assignees.filter((id) => id !== userId));
    }
  }

  getAssigneesNames(task: Task): string {
    if (!this.project || !task.assignments) return '';
    const names = task.assignments?.map((a) => {
      const contributor = this.project?.contributors.find(
        (c) => c.id.idUser === a.id.userId
      );
      return contributor ? contributor.userName : 'Utilisateur inconnu';
    });
    return names.join(', ');
  }

  onAddTaskSubmit() {
    this.isTaskSubmitted = true;
    if (this.addTaskForm.invalid) return;

    const { name, description, dueDate, endDate, priority, assigneeIds } =
      this.addTaskForm.value.taskData;

    const newTask: LocalTask = {
      name,
      description,
      dueDate,
      endDate,
      priority,
      assigneeIds,
    };

    console.log('new taskkk', newTask);

    this.taskService
      .createTask(this.projectId, newTask)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (createdTask) => {
          this.hideAddTaskBlock();
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
}
