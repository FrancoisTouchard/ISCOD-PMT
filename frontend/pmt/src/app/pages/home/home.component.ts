import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule, NgClass, NgIf } from '@angular/common';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

@Component({
  selector: 'app-home',
  imports: [CommonModule, ReactiveFormsModule, NgClass, NgIf],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  errorMessage = '';
  loading = false;
  projectCreationForm: FormGroup;
  modal = false;
  projects: Project[] = [];
  submitted = false;
  userId: string = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private projectService: ProjectService
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
      this.errorMessage = 'Utilisateur non identifié';
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

  loadProjects(): void {
    this.loading = true;
    this.projectService
      .getProjectsByContributeur(this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.projects = this.projectService.projects;
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
      .subscribe({
        next: (data) => {
          console.log(`Projet ${name} créé avec succès`);
        },
        error: (err) => {
          console.error('Erreur lors de la création du projet: ', err);
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
