import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
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
  private destroy$ = new Subject<void>();
  errorMessage = '';
  getRoleLabel = getRoleString;
  loading = false;
  isAddContributorBlockVisible = false;
  project: Project | null = null;
  projectId = '';
  Role = Role;
  roles = Object.values(Role);
  submitted = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private contributorService: ContributorService,
    private projectService: ProjectService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.addContributorForm = new FormGroup({
      contributorData: new FormGroup({
        email: new FormControl(null, [Validators.required, Validators.email]),
        role: new FormControl(Role.ADMINISTRATEUR, [Validators.required]),
      }),
    });
  }

  ngOnInit(): void {
    this.projectId = this.route.snapshot.paramMap.get('id') || '';
    if (this.projectId) {
      this.loadProject();
    } else {
      this.errorMessage = 'Projet non trouvé';
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

  goToHomePage() {
    this.router.navigate(['/home']);
  }

  logOut(): void {
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
    this.submitted = false;
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  loadProject(): void {
    this.loading = true;
    this.projectService
      .getProjectById(this.projectId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.project = data;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du chargement du projet';
          this.loading = false;
        },
      });
  }

  // Méthodes de gestion des contributeurs

  onAddContributorSubmit() {
    this.submitted = true;
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
        error: (err) => {
          console.error("Erreur lors de l'ajout du contributeur: ", err);
          this.toastService.showToast(
            "Erreur lors de l'ajout du contributeur",
            'error'
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
          this.errorMessage = 'Erreur lors de la modification du rôle';
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
