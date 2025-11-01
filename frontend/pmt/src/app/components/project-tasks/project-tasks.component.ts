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

  addTaskForm: FormGroup;
  isTaskSubmitted = false;
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
