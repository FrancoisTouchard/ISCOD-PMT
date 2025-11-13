import { CommonModule, NgClass, NgIf, NgStyle } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { LocalProject } from '../../../models/project.model';

@Component({
  selector: 'app-project-modal',
  imports: [CommonModule, ReactiveFormsModule, NgClass, NgIf, NgStyle],
  templateUrl: './project-modal.component.html',
  styleUrl: './project-modal.component.scss',
})
export class ProjectModalComponent {
  @Input() isOpen = false;

  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<LocalProject>();

  projectCreationForm: FormGroup;

  constructor() {
    this.projectCreationForm = new FormGroup({
      projectData: new FormGroup({
        name: new FormControl(null, [
          Validators.required,
          Validators.maxLength(100),
        ]),
        description: new FormControl(null, [Validators.maxLength(250)]),
        startDate: new FormControl(null),
      }),
    });
  }

  get projectData() {
    return this.projectCreationForm.get('projectData') as FormGroup;
  }

  get name() {
    return this.projectCreationForm.get('projectData.name') as FormControl;
  }

  get description() {
    return this.projectCreationForm.get(
      'projectData.description'
    ) as FormControl;
  }

  get startDate() {
    return this.projectCreationForm.get('projectData.startDate') as FormControl;
  }

  onSubmit(): void {
    if (this.projectCreationForm.invalid) return;

    const { name, description, startDate } =
      this.projectCreationForm.value.projectData;

    this.saved.emit({ name, description, startDate });
    this.projectCreationForm.reset();
  }

  close(): void {
    this.projectCreationForm.reset({
      name: null,
      description: null,
      startDate: null,
    });
    this.closed.emit();
  }
}
