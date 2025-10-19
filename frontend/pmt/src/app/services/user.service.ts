import { Injectable, OnDestroy } from '@angular/core';
import { ApiService } from './api.service';
import { Subscription, tap } from 'rxjs';
import { LocalUser, User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService implements OnDestroy {
  users: User[] = [];
  private subscription?: Subscription;

  constructor(private apiService: ApiService) {
    this.subscription = this.getUsers().subscribe();
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  addUser(nom: string, email: string, password: string) {
    const newLocalUser: LocalUser = { nom, email, password };
    return this.apiService.postUser(newLocalUser).pipe(
      tap((newUser) => {
        this.users.push(newUser);
      })
    );
  }

  getUsers() {
    return this.apiService.getUsers().pipe(
      tap((users) => {
        this.users = users;
      })
    );
  }
}
