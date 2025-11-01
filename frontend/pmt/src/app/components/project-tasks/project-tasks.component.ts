import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
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

@Component({
  selector: 'app-project-tasks',
  standalone: true,
  imports: [
    CommonModule,
    NgbDropdownModule,
    ReactiveFormsModule,
    NgClass,
    NgFor,
    NgIf,
  ],
  templateUrl: './project-tasks.component.html',
  styleUrl: './project-tasks.component.scss',
})
export class ProjectTasksComponent {
  @Input() project!: Project | null;
  @Input() tasks: Task[] = [];
  @Input() showAddForm = false;

  @Output() taskAdded = new EventEmitter<LocalTask>();
  @Output() formClosed = new EventEmitter<void>();
  @Output() taskUpdated = new EventEmitter<{
    taskId: string;
    updatedTask: Task;
  }>();

  addTaskForm: FormGroup;
  editTaskForm: FormGroup;
  isTaskSubmitted = false;
  isEditMode = false;
  selectedTask: Task | null = null;
  Priority = Priority;

  constructor() {
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

    this.editTaskForm = new FormGroup({
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

  get editTaskName() {
    return this.editTaskForm.get('taskData.name') as FormControl;
  }

  get editTaskDescription() {
    return this.editTaskForm.get('taskData.description') as FormControl;
  }

  get editTaskDueDate() {
    return this.editTaskForm.get('taskData.dueDate') as FormControl;
  }

  get editTaskEndDate() {
    return this.editTaskForm.get('taskData.endDate') as FormControl;
  }

  get editTaskPriority() {
    return this.editTaskForm.get('taskData.priority') as FormControl;
  }

  get editTaskAssigneeIds() {
    return this.editTaskForm.get('taskData.assigneeIds') as FormControl;
  }

  hideAddTaskBlock() {
    this.isTaskSubmitted = false;
    this.addTaskForm.reset({
      taskData: {
        name: null,
        description: null,
        dueDate: null,
        endDate: null,
        priority: Priority.MEDIUM,
        assigneeIds: [],
      },
    });
    this.formClosed.emit();
  }

  onAssigneeToggle(userId: string, checked: boolean) {
    const assignees = this.taskAssigneeIds.value as string[];
    if (checked) {
      this.taskAssigneeIds.setValue([...assignees, userId]);
    } else {
      this.taskAssigneeIds.setValue(assignees.filter((id) => id !== userId));
    }
  }

  onEditAssigneeToggle(userId: string, checked: boolean) {
    const assignees = this.editTaskAssigneeIds.value as string[];
    if (checked) {
      this.editTaskAssigneeIds.setValue([...assignees, userId]);
    } else {
      this.editTaskAssigneeIds.setValue(
        assignees.filter((id) => id !== userId)
      );
    }
  }

  viewTask(task: Task) {
    console.log('wesssh');
    this.selectedTask = task;
    this.isEditMode = false;

    const assigneeIds = task.assignments?.map((a) => a.id.userId) || [];

    this.editTaskForm.patchValue({
      taskData: {
        name: task.name,
        description: task.description,
        dueDate: task.dueDate,
        endDate: task.endDate,
        priority: task.priority,
        assigneeIds: assigneeIds,
      },
    });
  }

  closeTaskModal() {
    this.selectedTask = null;
    this.isEditMode = false;
    this.editTaskForm.reset();
  }

  enableEditMode() {
    this.isEditMode = true;
  }

  cancelEdit() {
    this.isEditMode = false;
    if (this.selectedTask) {
      const assigneeIds =
        this.selectedTask.assignments?.map((a) => a.id.userId) || [];
      this.editTaskForm.patchValue({
        taskData: {
          name: this.selectedTask.name,
          description: this.selectedTask.description,
          dueDate: this.selectedTask.dueDate,
          endDate: this.selectedTask.endDate,
          priority: this.selectedTask.priority,
          assigneeIds: assigneeIds,
        },
      });
    }
  }

  onEditTaskSubmit() {
    if (this.editTaskForm.invalid || !this.selectedTask) return;

    const { name, description, dueDate, endDate, priority, assigneeIds } =
      this.editTaskForm.value.taskData;

    const updatedTask: Task = {
      id: this.selectedTask.id,
      name,
      description,
      dueDate,
      endDate,
      priority,
      assigneeIds: assigneeIds || [],
    };

    this.taskUpdated.emit({
      taskId: this.selectedTask.id,
      updatedTask,
    });

    this.closeTaskModal();
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

    this.taskAdded.emit(newTask);
    this.hideAddTaskBlock();
  }
}
