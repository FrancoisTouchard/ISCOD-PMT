import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _isLoggedIn = false;
  private _userId: string | null = null;

  constructor(private router: Router) {
    const loginState = localStorage.getItem('isLoggedIn');
    this._isLoggedIn = loginState === 'true';
    this._userId = localStorage.getItem('userId');
  }

  public get isLoggedIn() {
    return this._isLoggedIn;
  }

  public get userId() {
    return this._userId;
  }

  login(userId: string) {
    this._isLoggedIn = true;
    this._userId = userId;
    localStorage.setItem('isLoggedIn', 'true');
    localStorage.setItem('userId', userId);
    console.log('localstorrr', localStorage);
  }

  logout() {
    this._isLoggedIn = false;
    this._userId = null;
    localStorage.setItem('isLoggedIn', 'false');
    localStorage.removeItem('userId');
    this.router.navigate(['/login']);
  }
  isAuthenticated() {
    return new Promise<boolean>((resolve) => {
      setTimeout(() => {
        resolve(this.isLoggedIn);
      }, 200);
    });
  }
}
