import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Priority } from '../../models/priority.enum';
import { Project } from '../../models/project.model';
import { LocalTask, Task } from '../../models/task.model';
import { TaskModalComponent } from '../task-modal/task-modal.component';

@Component({
  selector: 'app-project-tasks',
  standalone: true,
  imports: [CommonModule, TaskModalComponent],
  templateUrl: './project-tasks.component.html',
  styleUrl: './project-tasks.component.scss',
})
export class ProjectTasksComponent {
  @Input() project!: Project | null;
  @Input() tasks: Task[] = [];
  @Input() showAddForm = false;

  @Output() taskAdded = new EventEmitter<LocalTask>();
  @Output() taskUpdated = new EventEmitter<{
    taskId: string;
    updatedTask: LocalTask;
  }>();
  @Output() formClosed = new EventEmitter<void>();

  selectedTask: Task | null = null;
  modalMode: 'create' | 'view' | 'edit' = 'view';
  isModalOpen = false;
  Priority = Priority;

  openCreateModal(): void {
    this.selectedTask = null;
    this.modalMode = 'create';
    this.isModalOpen = true;
  }

  viewTask(task: Task): void {
    this.selectedTask = task;
    this.modalMode = 'view';
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedTask = null;
    this.formClosed.emit();
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
}
