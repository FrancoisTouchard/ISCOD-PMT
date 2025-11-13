import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule, NgClass, NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { ProjectService } from '../../services/project.service';
import { ToastService } from '../../services/toast.service';
import { LocalProject, Project } from '../../models/project.model';
import { getRoleLabel } from '../../utils/labels';
import { ProjectModalComponent } from '../modals/project-modal/project-modal.component';
import { Role } from '../../models/role.enum';
import { ContributorService } from '../../services/contributor.service';

@Component({
  selector: 'app-home',
  imports: [CommonModule, ProjectModalComponent, NgClass, NgIf, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  getRoleLabel = getRoleLabel;
  isModalOpen = false;
  loading = false;
  projects: Project[] = [];
  userId: string = '';

  constructor(
    private authService: AuthService,
    private contributorService: ContributorService,
    private projectService: ProjectService,
    private toastService: ToastService
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

  private getContributorRoleForProject(project: Project): Role | null {
    const contributor = project.contributors.find(
      (c) => c.id.idUser === this.userId
    );
    return contributor?.role || null;
  }

  getUserRoleLabel(project: Project): { color: string; label: string } {
    const role = this.getContributorRoleForProject(project);
    return role ? getRoleLabel(role) : { color: '', label: '' };
  }

  setUserRoleForProject(project: Project): void {
    const role = this.getContributorRoleForProject(project);
    if (role) {
      this.contributorService.setCurrentContributorRole(role);
    }
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
