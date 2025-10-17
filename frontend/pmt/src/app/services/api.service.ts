import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { LocalUser } from './user.service';

const API_URL = 'http://localhost:8080/';

export interface PostResponse {
  name: string;
}

export interface GetUserResponse {
  [key: string]: LocalUser;
}

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private httpClient: HttpClient) {}

  catchError(error: HttpErrorResponse) {
    alert(error.error.error);
    return throwError(() => error);
  }

  postUser(user: LocalUser) {
    return this.httpClient
      .post<PostResponse>(`${API_URL}users`, user)
      .pipe(catchError(this.catchError));
  }

  getUsers() {
    let queryParams = new HttpParams();
    queryParams = queryParams.set('filter', true);
    return this.httpClient
      .get<GetUserResponse>(`${API_URL}users`)
      .pipe(catchError(this.catchError));
  }

  deleteUser(id: string) {
    return this.httpClient
      .delete(`${API_URL}users/${id}`)
      .pipe(catchError(this.catchError));
  }
}
