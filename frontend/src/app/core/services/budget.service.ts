import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Budget, BudgetSummary } from '../models/budget.model';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root',
})
export class BudgetService {
  private apiUrl = `${environment.apiUrl}/budgets`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  getAll(): Observable<Budget[]> {
    return this.http.get<Budget[]>(this.apiUrl).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch budgets');
        return throwError(() => error);
      }),
    );
  }

  getActive(): Observable<Budget[]> {
    return this.http.get<Budget[]>(`${this.apiUrl}/active`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch active budgets');
        return throwError(() => error);
      }),
    );
  }

  getById(id: number | string): Observable<Budget> {
    return this.http.get<Budget>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch budget');
        return throwError(() => error);
      }),
    );
  }

  create(budget: Budget): Observable<Budget> {
    return this.http.post<Budget>(this.apiUrl, budget).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to create budget');
        return throwError(() => error);
      }),
    );
  }

  update(id: number | string, budget: Budget): Observable<Budget> {
    return this.http.put<Budget>(`${this.apiUrl}/${id}`, budget).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to update budget');
        return throwError(() => error);
      }),
    );
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to delete budget');
        return throwError(() => error);
      }),
    );
  }

  getSummary(period: string): Observable<BudgetSummary> {
    return this.http.get<BudgetSummary>(`${this.apiUrl}/summary/${period}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch budget summary');
        return throwError(() => error);
      }),
    );
  }
}