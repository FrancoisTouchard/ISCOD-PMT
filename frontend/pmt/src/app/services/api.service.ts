import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { LocalUser, User } from '../models/user.model';
import { Projet } from '../models/projet.model';

const API_URL = 'http://localhost:8080/';

export interface PostUserResponse extends User {}

export interface GetUserResponse extends User {
  projets: Projet[];
}

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

  private handleError(error: HttpErrorResponse) {
    console.error('Erreur API:', error);
    return throwError(() => error);
  }
}
