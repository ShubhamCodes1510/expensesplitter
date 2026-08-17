import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpErrorResponse,
  HttpEvent,
  HttpRequest,
  HttpHandlerFn,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const token = authService.getToken();
  let authReq = req;

  if (token) {
    authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  return next(authReq).pipe(
    catchError((error): Observable<HttpEvent<any>> => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        authService.logout();
        window.location.href = '/auth/login';
      }
      return throwError(() => error);
    }),
  );
};
