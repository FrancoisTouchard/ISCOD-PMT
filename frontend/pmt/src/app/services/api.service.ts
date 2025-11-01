import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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

  // méthodes user

  postUser(user: LocalUser) {
    return this.httpClient.post<User>(`${API_URL}users`, user);
  }

  getUsers() {
    return this.httpClient.get<User[]>(`${API_URL}users`);
  }

  deleteUser(id: string) {
    return this.httpClient.delete(`${API_URL}users/${id}`);
  }

  // méthodes projet

  getProjectsByContributeur(userId: string) {
    return this.httpClient.get<Project[]>(
      `${API_URL}projects/my-projects/${userId}`
    );
  }

  getProjectById(projectId: string) {
    return this.httpClient.get<Project>(`${API_URL}projects/${projectId}`);
  }

  postProject(project: LocalProject, creatorId: string) {
    return this.httpClient.post<Project>(
      `${API_URL}projects?creatorId=${creatorId}`,
      project
    );
  }

  deleteProject(id: string) {
    return this.httpClient.delete<Project[]>(`${API_URL}projects/${id}`);
  }

  // méthodes contributeur

  patchContributorRole(projectId: string, userId: string, newRole: Role) {
    return this.httpClient.patch<Contributor>(
      `${API_URL}contributors/project/${projectId}/user/${userId}`,
      {
        role: newRole,
      }
    );
  }

  postContributor(projectId: string, email: string, role: Role) {
    return this.httpClient.post<Contributor>(
      `${API_URL}contributors/project/${projectId}`,
      {
        email,
        role,
      }
    );
  }

  deleteContributor(projectId: string, userId: string): Observable<void> {
    return this.httpClient.delete<void>(
      `${API_URL}contributors/project/${projectId}/user/${userId}`
    );
  }

  // méthodes tâches

  getTasksByProjectId(projectId: string) {
    return this.httpClient.get<Task[]>(`${API_URL}tasks/project/${projectId}`);
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

    return this.httpClient.post<Task>(
      `${API_URL}tasks/project/${projectId}`,
      payload
    );
  }

  patchTask(
    projectId: string,
    taskId: string,
    updatedTask: Task
  ): Observable<Task> {
    const payload = {
      name: updatedTask.name,
      description: updatedTask.description,
      dueDate: updatedTask.dueDate,
      endDate: updatedTask.endDate,
      priority: updatedTask.priority,
      assigneeIds: updatedTask.assigneeIds,
    };

    return this.httpClient.patch<Task>(
      `${API_URL}tasks/project/${projectId}/${taskId}`,
      payload
    );
  }

  deleteTaskById(taskId: string) {
    return this.httpClient.delete<Task[]>(`${API_URL}tasks/${taskId}`);
  }
}
