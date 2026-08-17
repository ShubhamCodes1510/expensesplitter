import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unexpected error occurred';

      if (error.error instanceof ErrorEvent) {
        errorMessage = error.error.message;
      } else {
        switch (error.status) {
          case 400:
            errorMessage = error.error?.message || 'Bad request. Please check your input.';
            break;
          case 401:
            errorMessage = 'Your session has expired. Please login again.';
            sessionStorage.removeItem('currentUser');
            window.location.href = '/auth/login';
            break;
          case 403:
            errorMessage = 'You do not have permission to perform this action.';
            break;
          case 404:
            errorMessage = error.error?.message || 'The requested resource was not found.';
            break;
          case 409:
            errorMessage = error.error?.message || 'Conflict: The resource already exists.';
            break;
          case 422:
            errorMessage = error.error?.message || 'Validation error. Please check your input.';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
          case 503:
            errorMessage = 'Service unavailable. Please try again later.';
            break;
          default:
            errorMessage = error.error?.message || `Error: ${error.message}`;
        }
      }

      console.error('HTTP Error:', {
        status: error.status,
        message: errorMessage,
        url: req.url,
        error: error.error
      });

      notificationService.showError(errorMessage);

      const wrappedError = new Error(errorMessage);
      (wrappedError as any).status = error.status;
      (wrappedError as any).originalError = error;
      return throwError(() => wrappedError);
    })
  );
};
