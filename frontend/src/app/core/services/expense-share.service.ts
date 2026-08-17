import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';
import { ExpenseShare } from '../models/expense-share.model';

@Injectable({
  providedIn: 'root',
})
export class ExpenseShareService {
  private apiUrl = `${environment.apiUrl}/expense-shares`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  getAll(): Observable<ExpenseShare[]> {
    return this.http.get<ExpenseShare[]>(this.apiUrl).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch expense shares');
        return throwError(() => error);
      }),
    );
  }

  getById(id: string): Observable<ExpenseShare> {
    return this.http.get<ExpenseShare>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch expense share');
        return throwError(() => error);
      }),
    );
  }

  getByExpense(expenseId: string): Observable<ExpenseShare[]> {
    return this.http.get<ExpenseShare[]>(`${this.apiUrl}/expense/${expenseId}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch shares for expense');
        return throwError(() => error);
      }),
    );
  }

  getByUser(userId: string): Observable<ExpenseShare[]> {
    return this.http.get<ExpenseShare[]>(`${this.apiUrl}/user/${userId}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch user shares');
        return throwError(() => error);
      }),
    );
  }

  getUnsettledByUser(userId: string): Observable<ExpenseShare[]> {
    return this.http.get<ExpenseShare[]>(`${this.apiUrl}/unsettled/user/${userId}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch unsettled shares');
        return throwError(() => error);
      }),
    );
  }

  create(expenseShare: ExpenseShare): Observable<ExpenseShare> {
    return this.http.post<ExpenseShare>(this.apiUrl, expenseShare).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to create expense share');
        return throwError(() => error);
      }),
    );
  }

  update(id: string, expenseShare: ExpenseShare): Observable<ExpenseShare> {
    return this.http.put<ExpenseShare>(`${this.apiUrl}/${id}`, expenseShare).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to update expense share');
        return throwError(() => error);
      }),
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to delete expense share');
        return throwError(() => error);
      }),
    );
  }
}
