import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const currentUser = authService.currentUserValue;
  if (currentUser && currentUser.token) {
    return true;
  }

  // Redirect to login page with return URL
  router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
