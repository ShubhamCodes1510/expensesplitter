import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';

export interface Category {
  id?: string;
  name: string;
  description?: string;
}

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiUrl = `${environment.apiUrl}/categories`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch categories');
        return throwError(() => error);
      }),
    );
  }

  getById(id: string): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch category');
        return throwError(() => error);
      }),
    );
  }

  create(category: Category): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to create category');
        return throwError(() => error);
      }),
    );
  }

  update(id: string, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${id}`, category).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to update category');
        return throwError(() => error);
      }),
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to delete category');
        return throwError(() => error);
      }),
    );
  }
}
