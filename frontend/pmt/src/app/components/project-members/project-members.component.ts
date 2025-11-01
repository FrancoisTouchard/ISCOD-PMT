import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { Contributor } from '../../models/contributor.model';
import { Project } from '../../models/project.model';
import { Role } from '../../models/role.enum';
import { getRoleString } from '../../utils/labels';

export interface ContributorAddData {
  email: string;
  role: Role;
}

export interface ContributorRoleUpdateData {
  contributorId: string;
  newRole: Role;
}

@Component({
  selector: 'app-project-members',
  standalone: true,
  imports: [
    CommonModule,
    NgbDropdownModule,
    ReactiveFormsModule,
    NgClass,
    NgFor,
    NgIf,
  ],
  templateUrl: './project-members.component.html',
  styleUrl: './project-members.component.scss',
})
export class ProjectMembersComponent {
  @Input() project!: Project | null;
  @Input() showAddForm = false;

  @Output() contributorAdded = new EventEmitter<ContributorAddData>();
  @Output() contributorDeleted = new EventEmitter<string>();
  @Output() contributorRoleUpdated =
    new EventEmitter<ContributorRoleUpdateData>();
  @Output() formClosed = new EventEmitter<void>();

  addContributorForm: FormGroup;
  isContributorSubmitted = false;
  getRoleLabel = getRoleString;
  Role = Role;
  roles = Object.values(Role);

  constructor() {
    this.addContributorForm = new FormGroup({
      contributorData: new FormGroup({
        email: new FormControl(null, [Validators.required, Validators.email]),
        role: new FormControl(Role.OBSERVATEUR, [Validators.required]),
      }),
    });
  }

  get email() {
    return this.addContributorForm.get('contributorData.email') as FormControl;
  }

  get role() {
    return this.addContributorForm.get('contributorData.role') as FormControl;
  }

  hideAddContributorBlock() {
    this.addContributorForm.reset({
      contributorData: {
        email: null,
        role: Role.OBSERVATEUR,
      },
    });
    this.isContributorSubmitted = false;
    this.formClosed.emit();
  }

  onAddContributorSubmit() {
    this.isContributorSubmitted = true;
    if (this.addContributorForm.invalid) return;

    const email = this.email.value;
    const role = this.role.value;

    this.contributorAdded.emit({ email, role });
    this.hideAddContributorBlock();
  }

  onDeleteContributor(contributor: Contributor): void {
    if (
      !confirm(
        `Voulez-vous vraiment retirer ${contributor.userName} du projet ?`
      )
    ) {
      return;
    }

    this.contributorDeleted.emit(contributor.id.idUser);
  }

  onUpdateContributorRole(contributor: Contributor, newRole: Role): void {
    if (contributor.role === newRole) return;

    this.contributorRoleUpdated.emit({
      contributorId: contributor.id.idUser,
      newRole,
    });
  }
}
