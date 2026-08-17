import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';
import { Settlement, SettlementBalance } from '../models/settlement.model';

@Injectable({
  providedIn: 'root',
})
export class SettlementsService {
  private apiUrl = `${environment.apiUrl}/settlements`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  getBalances(): Observable<SettlementBalance[]> {
    return this.http.get<SettlementBalance[]>(`${this.apiUrl}/balances`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch balances');
        return throwError(() => error);
      }),
    );
  }

  calculateSettlements(): Observable<Settlement[]> {
    return this.http.get<Settlement[]>(`${this.apiUrl}/calculate`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to calculate settlements');
        return throwError(() => error);
      }),
    );
  }

  getHistory(): Observable<Settlement[]> {
    return this.http.get<Settlement[]>(this.apiUrl).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch settlement history');
        return throwError(() => error);
      }),
    );
  }

  markAsPaid(settlementId: string): Observable<Settlement> {
    return this.http.post<Settlement>(`${this.apiUrl}/${settlementId}/pay`, {}).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to mark payment');
        return throwError(() => error);
      }),
    );
  }

  processPayment(fromUserId: string, toUserId: string, amount: number): Observable<Settlement> {
    return this.http
      .post<Settlement>(`${this.apiUrl}/process-payment`, {
        fromUserId,
        toUserId,
        amount,
      })
      .pipe(
        catchError((error) => {
          this.notificationService.showError('Failed to process payment');
          return throwError(() => error);
        }),
      );
  }
}
