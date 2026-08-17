import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch users');
        return throwError(() => error);
      }),
    );
  }

  getById(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch user');
        return throwError(() => error);
      }),
    );
  }

  create(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to create user');
        return throwError(() => error);
      }),
    );
  }

  update(id: string, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to update user');
        return throwError(() => error);
      }),
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to delete user');
        return throwError(() => error);
      }),
    );
  }
}
