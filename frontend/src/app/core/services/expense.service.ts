import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Expense } from '../models/expense.model';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';

export interface ExpenseSummary {
  totalExpenses: number;
  amountOwed: number;
  amountLent: number;
  userId?: number;
  userName?: string;
  netBalance?: number;
  totalOwed?: number;
  totalSettled?: number;
}

export interface ExpenseFilter {
  categoryId?: number;
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
  paidByUserId?: number;
}

@Injectable({
  providedIn: 'root',
})
export class ExpenseService {
  private apiUrl = `${environment.apiUrl}/expenses`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  getAll(): Observable<Expense[]> {
    return this.http.get<Expense[]>(this.apiUrl).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch expenses');
        return throwError(() => error);
      }),
    );
  }

  getFiltered(filter: ExpenseFilter): Observable<Expense[]> {
    let params = new HttpParams();
    
    if (filter.categoryId) {
      params = params.set('categoryId', filter.categoryId.toString());
    }
    if (filter.startDate) {
      params = params.set('startDate', filter.startDate);
    }
    if (filter.endDate) {
      params = params.set('endDate', filter.endDate);
    }
    if (filter.minAmount) {
      params = params.set('minAmount', filter.minAmount.toString());
    }
    if (filter.maxAmount) {
      params = params.set('maxAmount', filter.maxAmount.toString());
    }
    if (filter.paidByUserId) {
      params = params.set('paidByUserId', filter.paidByUserId.toString());
    }

    return this.http.get<Expense[]>(this.apiUrl, { params }).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch filtered expenses');
        return throwError(() => error);
      }),
    );
  }

  getById(id: string): Observable<Expense> {
    return this.http.get<Expense>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch expense');
        return throwError(() => error);
      }),
    );
  }

  create(expense: Expense): Observable<Expense> {
    return this.http.post<Expense>(this.apiUrl, expense).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to create expense');
        return throwError(() => error);
      }),
    );
  }

  update(id: string, expense: Expense): Observable<Expense> {
    return this.http.put<Expense>(`${this.apiUrl}/${id}`, expense).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to update expense');
        return throwError(() => error);
      }),
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to delete expense');
        return throwError(() => error);
      }),
    );
  }

  getSummary(): Observable<ExpenseSummary> {
    return this.http.get<ExpenseSummary>(`${this.apiUrl}/summary`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch summary');
        return throwError(() => error);
      }),
    );
  }

  getByUser(userId: string): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.apiUrl}/user/${userId}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch user expenses');
        return throwError(() => error);
      }),
    );
  }
}
