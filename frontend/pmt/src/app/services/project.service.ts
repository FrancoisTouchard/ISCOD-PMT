import { Injectable, OnDestroy } from '@angular/core';
import { LocalProject, Project } from '../models/project.model';
import { Observable, Subscription, tap } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class ProjectService implements OnDestroy {
  projects: Project[] = [];
  private subscription?: Subscription;

  constructor(private apiService: ApiService) {}

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  getProjectsByContributeur(userId: string) {
    return this.apiService.getProjectsByContributeur(userId).pipe(
      tap((projects) => {
        this.projects = projects;
      })
    );
  }

  getProjectById(projectId: string) {
    return this.apiService.getProjectById(projectId).pipe(
      tap((project) => {
        this.projects = [project];
      })
    );
  }

  createProject(
    name: string,
    description: string,
    startDate: string,
    creatorId: string
  ): Observable<Project> {
    const newLocalProject: LocalProject = { name, description, startDate };
    return this.apiService.postProject(newLocalProject, creatorId);
  }

  deleteProjectById(id: string) {
    return this.apiService.deleteProject(id).pipe(
      tap((projects) => {
        this.projects = projects;
      })
    );
  }
}
