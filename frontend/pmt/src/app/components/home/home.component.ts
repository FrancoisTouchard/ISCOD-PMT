import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule, NgClass, NgIf } from '@angular/common';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ProjectService } from '../../services/project.service';
import { ToastService } from '../../services/toast.service';
import { Project } from '../../models/project.model';
import { getRoleLabel } from '../../utils/labels';

@Component({
  selector: 'app-home',
  imports: [CommonModule, ReactiveFormsModule, NgClass, NgIf, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  getRoleLabel = getRoleLabel;
  loading = false;
  modal = false;
  projects: Project[] = [];
  projectCreationForm: FormGroup;
  submitted = false;
  userId: string = '';

  constructor(
    private authService: AuthService,
    private projectService: ProjectService,
    private toastService: ToastService
  ) {
    this.projectCreationForm = new FormGroup({
      projectData: new FormGroup({
        name: new FormControl(null, [Validators.required]),
        description: new FormControl(null),
        startDate: new FormControl(null),
      }),
    });
  }

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

  getUserRoleLabel(project: Project): string {
    const contributor = project.contributors.find(
      (c) => c.id.idUser === this.userId
    );
    return contributor ? getRoleLabel(contributor.role).label : '';
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
    this.modal = true;
  }

  closeProjectCreationModal() {
    this.modal = false;
  }

  onProjectCreationSubmit() {
    this.submitted = true;
    if (this.projectCreationForm.invalid) return;
    const name = this.name.value;
    const description = this.description.value;
    const startDate = this.startDate.value;
    console.log('projet submitedxx', name, description, startDate, this.userId);
    this.projectService
      .createProject(name, description, startDate, this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          console.log('dataxxx', data);
          this.closeProjectCreationModal();
          this.projectCreationForm.reset();
          this.submitted = false;
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
