import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';

export interface RecurringBill {
  id?: string;
  name: string;
  amount: number;
  frequency: string;
  nextDueDate: string;
  lastPaymentDate?: string;
  description?: string;
}

@Injectable({
  providedIn: 'root',
})
export class RecurringBillService {
  private apiUrl = `${environment.apiUrl}/recurring-bills`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  getAll(): Observable<RecurringBill[]> {
    return this.http.get<RecurringBill[]>(this.apiUrl).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch recurring bills');
        return throwError(() => error);
      }),
    );
  }

  getById(id: string): Observable<RecurringBill> {
    return this.http.get<RecurringBill>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch recurring bill');
        return throwError(() => error);
      }),
    );
  }

  getUpcoming(): Observable<RecurringBill[]> {
    return this.http.get<RecurringBill[]>(`${this.apiUrl}/upcoming`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch upcoming recurring bills');
        return throwError(() => error);
      }),
    );
  }

  create(bill: RecurringBill): Observable<RecurringBill> {
    return this.http.post<RecurringBill>(this.apiUrl, bill).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to create recurring bill');
        return throwError(() => error);
      }),
    );
  }

  markAsPaid(id: string): Observable<RecurringBill> {
    return this.http.put<RecurringBill>(`${this.apiUrl}/${id}/pay`, {}).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to mark recurring bill as paid');
        return throwError(() => error);
      }),
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to delete recurring bill');
        return throwError(() => error);
      }),
    );
  }
}
