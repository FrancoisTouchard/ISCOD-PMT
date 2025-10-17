import { Injectable } from '@angular/core';
import { ApiService, GetUserResponse, PostResponse } from './api.service';
import { map, tap } from 'rxjs';

export interface LocalUser {
  nom: string;
  email: string;
  password: string;
}

export interface User extends LocalUser {
  id: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  users: User[] = [];

  constructor(private apiService: ApiService) {
    this.getUsers();
  }

  addUser(nom: string, email: string, password: string) {
    const newLocalUser: LocalUser = { nom, email, password };
    console.log('user in addUser', newLocalUser);
    return this.apiService.postUser(newLocalUser).pipe(
      map((res: PostResponse) => {
        const newUser: User = { ...newLocalUser, id: res.name };
        this.users.push(newUser);
        return newUser;
      })
    );
  }

  getUsers() {
    return this.apiService.getUsers().pipe(
      map((res: GetUserResponse) => {
        const users: User[] = [];
        Object.entries(res).forEach(([id, user]) => {
          users.push({
            ...user,
            id,
          });
        });
        console.log('users in userService.getUsers---', users);
        return users;
      }),
      tap((users: User[]) => {
        this.users = users;
      })
    );
  }
}
