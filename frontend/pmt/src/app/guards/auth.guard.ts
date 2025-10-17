import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = async (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isLoggedIn = await authService.isAuthenticated();
  const currentUrl = state.url;

  if (isLoggedIn && (currentUrl === '/login' || currentUrl === '/signin')) {
    router.navigate(['/home']);
    return false;
  }

  if (!isLoggedIn && currentUrl === '/home') {
    router.navigate(['']);
    return false;
  }
  return true;
};
