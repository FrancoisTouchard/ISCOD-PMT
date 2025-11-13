import { CommonModule, NgIf, NgStyle } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { Priority } from '../../../models/priority.enum';
import { Project } from '../../../models/project.model';
import { LocalTask, Task } from '../../../models/task.model';
import { TaskModalComponent } from '../../modals/task-modal/task-modal.component';
import { TaskStatus } from '../../../models/taskStatus.enum';
import { getPriorityLabel, getStatusLabel } from '../../../utils/labels';
import { HistoryModalComponent } from '../../modals/history-modal/history-modal.component';
import { HistoryService } from '../../../services/history.service';
import { HistoryEntry } from '../../../models/historyEntry.model';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { Role } from '../../../models/role.enum';

@Component({
  selector: 'app-project-tasks',
  standalone: true,
  imports: [
    CommonModule,
    NgbDropdownModule,
    HistoryModalComponent,
    TaskModalComponent,
    NgIf,
    NgStyle,
  ],
  templateUrl: './project-tasks.component.html',
  styleUrl: './project-tasks.component.scss',
})
export class ProjectTasksComponent implements OnDestroy {
  @Input() project!: Project | null;
  @Input() tasks: Task[] = [];
  @Input() loading = false;
  @Input() currentUserRole: Role | null = null;

  @Output() taskAdded = new EventEmitter<LocalTask>();
  @Output() taskUpdated = new EventEmitter<{
    taskId: string;
    updatedTask: LocalTask;
  }>();
  @Output() taskDeleted = new EventEmitter<string>();
  @Output() formClosed = new EventEmitter<void>();

  private destroy$ = new Subject<void>();
  selectedTask: Task | null = null;
  selectedTaskHistory: HistoryEntry[] = [];
  modalMode: 'create' | 'view' | 'edit' = 'view';
  isHistoryModalOpen = false;
  isModalOpen = false;
  loadingHistory = false;
  Priority = Priority;
  selectedStatus: TaskStatus | 'ALL' = 'ALL';
  statusList = Object.values(TaskStatus);
  TaskStatus = TaskStatus;
  getPriorityLabel = getPriorityLabel;
  getStatusLabel = getStatusLabel;

  constructor(private historyService: HistoryService) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  openModal(): void {
    this.selectedTask = null;
    this.modalMode = 'create';
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedTask = null;
    this.modalMode = 'view';
    this.formClosed.emit();
  }

  openHistoryModal(task: Task): void {
    this.selectedTask = task;
    this.loadingHistory = true;
    this.isHistoryModalOpen = true;

    // Charger l'historique de la tâche sélectionnée
    this.historyService.getHistoryEntriesByTaskId(task.id).subscribe({
      next: (entries) => {
        this.selectedTaskHistory = entries;
        this.loadingHistory = false;
        console.log('entries histooo', entries);
      },
      error: (err) => {
        console.error("Erreur lors du chargement de l'historique:", err);
        this.loadingHistory = false;
        this.selectedTaskHistory = [];
      },
    });
  }

  closeHistoryModal(): void {
    this.isHistoryModalOpen = false;
    this.selectedTask = null;
    this.selectedTaskHistory = [];
  }

  viewTask(task: Task): void {
    this.selectedTask = task;
    this.modalMode = 'view';
    this.isModalOpen = true;
  }

  onTaskSaved(formData: LocalTask): void {
    if (this.modalMode === 'create') {
      this.taskAdded.emit(formData);
    } else if (this.selectedTask) {
      this.taskUpdated.emit({
        taskId: this.selectedTask.id,
        updatedTask: formData,
      });
    }
    this.closeModal();
  }

  onTaskDeleted(task: Task): void {
    if (!confirm(`Voulez-vous vraiment supprimer la tâche "${task.name}" ?`)) {
      return;
    }
    this.taskDeleted.emit(task.id);
  }

  getAssigneesNames(task: Task): string {
    if (!this.project || !task.assignments) return '';
    const names = task.assignments.map((a) => {
      const contributor = this.project?.contributors.find(
        (c) => c.id.idUser === a.id.userId
      );
      return contributor ? contributor.userName : 'Utilisateur inconnu';
    });
    return names.join(', ');
  }

  get filteredTasks() {
    if (this.selectedStatus === 'ALL') return this.tasks;
    return this.tasks.filter((task) => task.status === this.selectedStatus);
  }

  setStatusFilter(status: TaskStatus | 'ALL') {
    this.selectedStatus = status;
  }
}
