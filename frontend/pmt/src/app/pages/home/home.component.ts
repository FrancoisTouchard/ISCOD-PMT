import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ProjetService } from '../../services/projet.service';
import { Projet } from '../../models/projet.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit, OnDestroy {
  projets: Projet[] = [];
  userId: string = '';
  loading = false;
  modal = false;
  errorMessage = '';
  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private authService: AuthService,
    private projetService: ProjetService
  ) {}

  ngOnInit(): void {
    this.userId = localStorage.getItem('userId') || '';
    if (this.userId) {
      this.loadProjets();
    } else {
      this.errorMessage = 'Utilisateur non identifié';
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadProjets(): void {
    this.loading = true;
    this.projetService
      .getProjectsByContributeur(this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.projets = this.projetService.projets;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du chargement des projets';
          this.loading = false;
        },
      });
  }

  logOutAndRedirect(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  openCreateProjectModal() {
    this.modal = true;
  }

  closeModal() {
    this.modal = false;
  }

  deleteProject(id: string): void {
    if (!confirm('Voulez-vous vraiment supprimer ce projet ?')) {
      return;
    }

    this.projetService
      .deleteProjectById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.projets = this.projets.filter((p) => p.id !== id);
        },
        error: () => {
          this.errorMessage = 'Erreur lors de la suppression du projet';
        },
      });
  }

  getRoleLabel(role: string): string {
    const roleMap: { [key: string]: string } = {
      ADMINISTRATEUR: 'Administrateur',
      MEMBRE: 'Membre',
      OBSERVATEUR: 'Observateur',
    };
    return roleMap[role] || role;
  }
}
