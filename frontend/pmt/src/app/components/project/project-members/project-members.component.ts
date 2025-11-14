import { CommonModule, NgFor, NgIf, NgStyle } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { Contributor } from '../../../models/contributor.model';
import { Project } from '../../../models/project.model';
import { Role } from '../../../models/role.enum';
import { getRoleLabel } from '../../../utils/labels.utils';
import { getCurrentUserRole } from '../../../utils/role.utils';

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
    NgFor,
    NgIf,
    NgStyle,
  ],
  templateUrl: './project-members.component.html',
  styleUrl: './project-members.component.scss',
})
export class ProjectMembersComponent implements OnInit {
  @Input() project!: Project | null;
  @Input() showAddForm = false;
  @Input() currentUserRole!: Role | null;

  @Output() contributorDeleted = new EventEmitter<string>();
  @Output() contributorRoleUpdated =
    new EventEmitter<ContributorRoleUpdateData>();

  getCurrentUserRole = getCurrentUserRole;
  getRoleLabel = getRoleLabel;
  Role = Role;
  roles = Object.values(Role);

  constructor() {}
  ngOnInit(): void {
    const userId = localStorage.getItem('userId');
    if (userId) this.currentUserRole = getCurrentUserRole(this.project, userId);
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
