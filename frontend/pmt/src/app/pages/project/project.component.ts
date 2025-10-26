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

@Component({
  selector: 'app-project',
  imports: [CommonModule, NgbDropdownModule, NgClass, NgFor, NgIf],
  templateUrl: './project.component.html',
  styleUrl: './project.component.scss',
})
export class ProjectComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  errorMessage = '';
  loading = false;
  project: Project | null = null;
  projectId = '';
  roles = Object.values(Role);
  getRoleLabel = getRoleString;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private contributorService: ContributorService,
    private projectService: ProjectService,
    private router: Router
  ) {}

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

  logOut(): void {
    this.authService.logout();
  }

  goToHomePage() {
    this.router.navigate(['/home']);
  }

  loadProject(): void {
    this.loading = true;
    this.projectService
      .getProjectById(this.projectId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.project = data;
          console.log('projets in loadprojjj', this.project);
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du chargement du projet';
          this.loading = false;
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
          console.log('Contributeur reçu du backend:', updatedContributor); // 🔍 Debug
          this.loadProject();
          this.loading = false;
          console.log(`Rôle de ${contributor.userName} changé en ${newRole}`);
        },
        error: (err) => {
          this.errorMessage = 'Erreur lors de la modification du rôle';
          this.loading = false;
          console.error('Erreur:', err);
        },
      });
  }
}
