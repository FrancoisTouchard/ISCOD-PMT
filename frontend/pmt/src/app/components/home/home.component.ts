import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule, NgClass, NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { ProjectService } from '../../services/project.service';
import { ToastService } from '../../services/toast.service';
import { LocalProject, Project } from '../../models/project.model';
import { getRoleLabel } from '../../utils/labels';
import { ProjectModalComponent } from '../modals/project-modal/project-modal.component';
import { getCurrentUserRole } from '../../utils/role.utils';
import { Role } from '../../models/role.enum';

@Component({
  selector: 'app-home',
  imports: [CommonModule, ProjectModalComponent, NgClass, NgIf, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  currentUserRole: Role | null = null;
  getCurrentUserRole = getCurrentUserRole;
  getRoleLabel = getRoleLabel;
  isModalOpen = false;
  loading = false;
  projects: Project[] = [];
  userId: string = '';

  constructor(
    private authService: AuthService,
    private projectService: ProjectService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = localStorage.getItem('userId') || '';
    if (this.userId) {
      this.loadProjects();
    } else {
      this.toastService.showToast('Utilisateur non identifé', 'error');
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getUserRoleLabel(project: Project): { color: string; label: string } {
    this.currentUserRole = this.getCurrentUserRole(project, this.userId);
    return this.currentUserRole
      ? getRoleLabel(this.currentUserRole)
      : { color: '', label: '' };
  }

  loadProjects(): void {
    this.loading = true;
    this.projectService
      .getProjectsByContributeur(this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.projects = this.projectService.projects;
          console.log('projets in loadprojjj', this.projects);
          this.loading = false;
        },
        error: () => {
          this.toastService.showToast(
            'Erreur lors du chargement des données',
            'error'
          );
          this.loading = false;
        },
      });
  }

  logOut(): void {
    this.authService.logout();
  }

  navigateToProject(project: Project): void {
    this.router.navigate(['/project', project.id], {
      state: { project },
    });
  }

  openProjectCreationModal() {
    this.isModalOpen = true;
  }

  closeProjectCreationModal() {
    this.isModalOpen = false;
  }

  onProjectSaved(formData: LocalProject) {
    const { name, description, startDate } = formData;
    this.projectService
      .createProject(name, description, startDate, this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          console.log('dataxxx', data);
          this.closeProjectCreationModal();
          this.loadProjects();
          this.toastService.showToast(`Projet "${name}" créé !`, 'success');
        },
        error: (err) => {
          console.error('Erreur lors de la création du projet: ', err);
          this.toastService.showToast(
            'Erreur lors de la création du projet',
            'error'
          );
        },
      });
  }

  deleteProject(id: string): void {
    if (!confirm('Voulez-vous vraiment supprimer ce projet ?')) {
      return;
    }

    this.projectService
      .deleteProjectById(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.projects = this.projects.filter((p) => p.id !== id);
          this.toastService.showToast(
            'Projet supprimé avec succès !',
            'success'
          );
        },
        error: () => {
          this.toastService.showToast(
            'Erreur lors de la suppression du projet.',
            'error'
          );
        },
      });
  }
}
