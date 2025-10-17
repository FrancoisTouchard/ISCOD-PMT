import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _isLoggedIn = false;

  constructor() {
    const loginState = localStorage.getItem('isLoggedIn');
    this._isLoggedIn = loginState === 'true';
  }

  public get isLoggedIn() {
    return this._isLoggedIn;
  }

  login() {
    this._isLoggedIn = true;
    localStorage.setItem('isLoggedIn', 'true');
  }

  logout() {
    this._isLoggedIn = false;
    localStorage.setItem('isLoggedIn', 'false');
  }
  isAuthenticated() {
    return new Promise<boolean>((resolve) => {
      setTimeout(() => {
        resolve(this.isLoggedIn);
      }, 200);
    });
  }
}
