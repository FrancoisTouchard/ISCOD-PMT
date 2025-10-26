import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { LocalUser, User } from '../models/user.model';
import { LocalProject, Project } from '../models/project.model';
import { Role } from '../models/role.enum';
import { Contributor } from '../models/contributor.model';

const API_URL = 'http://localhost:8080/';

export interface PostUserResponse extends User {}
export interface PostProjectResponse extends Project {}

export interface GetUserResponse extends User {
  projects: Project[];
}

export interface GetProjectsResponse extends Project {}
export interface DeleteProjectsResponse extends Project {}

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private httpClient: HttpClient) {}

  postUser(user: LocalUser) {
    return this.httpClient
      .post<PostUserResponse>(`${API_URL}users`, user)
      .pipe(catchError((error) => this.handleError(error)));
  }

  getUsers() {
    return this.httpClient
      .get<GetUserResponse[]>(`${API_URL}users`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  deleteUser(id: string) {
    return this.httpClient
      .delete(`${API_URL}users/${id}`)
      .pipe(catchError((error) => this.handleError(error)));
  }

  getProjectsByContributeur(userId: string) {
    return this.httpClient
      .get<GetProjectsResponse[]>(`${API_URL}projects/my-projects/${userId}`)
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
      .delete<DeleteProjectsResponse[]>(`${API_URL}projects/${id}`)
      .pipe(catchError((error) => this.handleError(error)));
  }

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

  private handleError(error: HttpErrorResponse) {
    console.error('Erreur API:', error);
    return throwError(() => error);
  }
}
