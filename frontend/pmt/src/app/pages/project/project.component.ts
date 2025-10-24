import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-project',
  imports: [CommonModule, NgClass, NgFor, NgIf],
  templateUrl: './project.component.html',
  styleUrl: './project.component.scss',
})
export class ProjectComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  errorMessage = '';
  loading = false;
  project: Project | null = null;
  projectId = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
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
}
