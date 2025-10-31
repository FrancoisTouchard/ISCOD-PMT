import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { LocalUser, User } from '../models/user.model';
import { LocalProject, Project } from '../models/project.model';
import { Role } from '../models/role.enum';
import { Contributor } from '../models/contributor.model';
import { LocalTask, Task } from '../models/task.model';

const API_URL = 'http://localhost:8080/';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private httpClient: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    console.error('Erreur API:', error);
    return throwError(() => error);
  }

  // méthodes user

  postUser(user: LocalUser) {
    return this.httpClient
      .post<User>(`${API_URL}users`, user)
      .pipe(catchError((error) => this.handleError(error)));
  }

  getUsers() {
    return this.httpClient
      .get<User[]>(`${API_URL}users`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  deleteUser(id: string) {
    return this.httpClient
      .delete(`${API_URL}users/${id}`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  // méthodes projet

  getProjectsByContributeur(userId: string) {
    return this.httpClient
      .get<Project[]>(`${API_URL}projects/my-projects/${userId}`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  getProjectById(projectId: string) {
    return this.httpClient
      .get<Project>(`${API_URL}projects/${projectId}`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  postProject(project: LocalProject, creatorId: string) {
    return this.httpClient
      .post<Project>(`${API_URL}projects?creatorId=${creatorId}`, project)
      .pipe(catchError((error) => this.handleError(error)));
  }

  deleteProject(id: string) {
    return this.httpClient
      .delete<Project[]>(`${API_URL}projects/${id}`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  // méthodes contributeur

  patchContributorRole(projectId: string, userId: string, newRole: Role) {
    return this.httpClient
      .patch<Contributor>(
        `${API_URL}contributors/project/${projectId}/user/${userId}`,
        {
          role: newRole,
        }
      )
      .pipe(catchError((error) => this.handleError(error)));
  }

  postContributor(projectId: string, email: string, role: Role) {
    return this.httpClient
      .post<Contributor>(`${API_URL}contributors/project/${projectId}`, {
        email,
        role,
      })
      .pipe(catchError((error) => this.handleError(error)));
  }

  deleteContributor(projectId: string, userId: string): Observable<void> {
    return this.httpClient
      .delete<void>(
        `${API_URL}contributors/project/${projectId}/user/${userId}`
      )
      .pipe(catchError((error) => this.handleError(error)));
  }

  // méthodes tâches

  getTasksByProjectId(projectId: string) {
    return this.httpClient
      .get<Task[]>(`${API_URL}tasks/project/${projectId}`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  postTask(projectId: string, task: LocalTask): Observable<Task> {
    const payload = {
      name: task.name,
      description: task.description,
      dueDate: task.dueDate,
      endDate: task.endDate,
      priority: task.priority,
      assigneeIds: task.assigneeIds,
    };

    return this.httpClient
      .post<Task>(`${API_URL}tasks/project/${projectId}`, payload)
      .pipe(catchError((error) => this.handleError(error)));
  }

  deleteTaskById(taskId: string) {
    return this.httpClient
      .delete<Task[]>(`${API_URL}tasks/${taskId}`)
      .pipe(catchError((error) => this.handleError(error)));
  }
}
