import { CommonModule, NgClass, NgFor, NgIf, NgStyle } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { Priority } from '../../models/priority.enum';
import { Project } from '../../models/project.model';
import { LocalTask, Task } from '../../models/task.model';
import { TaskStatus } from '../../models/taskStatus.enum';
import { getPriorityLabel, getStatusLabel } from '../../utils/labels';

@Component({
  selector: 'app-task-modal',
  standalone: true,
  imports: [
    CommonModule,
    NgbDropdownModule,
    ReactiveFormsModule,
    NgClass,
    NgFor,
    NgIf,
    NgStyle,
  ],
  templateUrl: './task-modal.component.html',
  styleUrl: './task-modal.component.scss',
})
export class TaskModalComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() mode: 'create' | 'view' | 'edit' = 'view';
  @Input() task: Task | null = null;
  @Input() project: Project | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<LocalTask>();

  taskForm: FormGroup;
  isEditMode = false;
  Priority = Priority;
  TaskStatus = TaskStatus;
  getPriorityLabel = getPriorityLabel;
  getStatusLabel = getStatusLabel;

  constructor() {
    this.taskForm = new FormGroup({
      name: new FormControl(null, [Validators.required]),
      description: new FormControl(null),
      dueDate: new FormControl(null, [Validators.required]),
      endDate: new FormControl(null),
      priority: new FormControl(Priority.MEDIUM, [Validators.required]),
      status: new FormControl(TaskStatus.TODO, [Validators.required]),
      assigneeIds: new FormControl([]),
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.initializeForm();
    }

    if (changes['mode']) {
      this.isEditMode = this.mode === 'edit' || this.mode === 'create';
    }
  }

  get title(): string {
    if (this.mode === 'create') return 'Créer une tâche';
    if (this.mode === 'edit') return 'Modifier la tâche';
    return 'Détails de la tâche';
  }

  get assigneeIds() {
    return this.taskForm.get('assigneeIds') as FormControl;
  }

  get taskStatusOptions() {
    return Object.values(TaskStatus);
  }

  initializeForm(): void {
    if (this.mode === 'create') {
      this.taskForm.reset({
        name: null,
        description: null,
        dueDate: null,
        endDate: null,
        priority: Priority.MEDIUM,
        status: TaskStatus.TODO,
        assigneeIds: [],
      });
      this.isEditMode = true;
      // Si il y a une tâche on est en mode édition ou view
    } else if (this.task) {
      const assigneeIds = this.task.assignments?.map((a) => a.id.userId) || [];
      this.taskForm.patchValue({
        name: this.task.name,
        description: this.task.description,
        dueDate: this.task.dueDate,
        endDate: this.task.endDate,
        priority: this.task.priority,
        status: this.task.status,
        assigneeIds: assigneeIds,
      });
      this.isEditMode = this.mode === 'edit';
    }
  }

  onAssigneeToggle(userId: string, checked: boolean): void {
    const assignees = this.assigneeIds.value as string[];
    if (checked) {
      this.assigneeIds.setValue([...assignees, userId]);
    } else {
      this.assigneeIds.setValue(assignees.filter((id) => id !== userId));
    }
  }

  getAssigneesNames(): string {
    if (!this.project || !this.task?.assignments) return '';
    const names = this.task.assignments.map((a) => {
      const contributor = this.project?.contributors.find(
        (c) => c.id.idUser === a.id.userId
      );
      return contributor ? contributor.userName : 'Utilisateur inconnu';
    });
    return names.join(', ');
  }

  enableEditMode(): void {
    this.isEditMode = true;
    this.mode = 'edit';
  }

  cancelEdit(): void {
    if (this.mode === 'create') {
      this.close();
    } else {
      this.isEditMode = false;
      this.mode = 'view';
      this.initializeForm();
    }
  }

  onSubmit(): void {
    if (this.taskForm.invalid) return;

    const formData: LocalTask = {
      name: this.taskForm.value.name,
      description: this.taskForm.value.description,
      dueDate: this.taskForm.value.dueDate,
      endDate: this.taskForm.value.endDate,
      priority: this.taskForm.value.priority,
      status: this.taskForm.value.status,
      assigneeIds: this.taskForm.value.assigneeIds || [],
    };

    this.saved.emit(formData);

    if (this.mode === 'create') {
      this.close();
    } else {
      this.isEditMode = false;
      this.mode = 'view';
    }
  }

  close(): void {
    this.closed.emit();
  }
}
