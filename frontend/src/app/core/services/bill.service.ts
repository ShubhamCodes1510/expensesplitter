import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';

export interface Bill {
  id?: string;
  name: string;
  amount: number;
  dueDate: string;
  paid?: boolean;
  description?: string;
}

@Injectable({
  providedIn: 'root',
})
export class BillService {
  private apiUrl = `${environment.apiUrl}/bills`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  getAll(): Observable<Bill[]> {
    return this.http.get<Bill[]>(this.apiUrl).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch bills');
        return throwError(() => error);
      }),
    );
  }

  getById(id: string): Observable<Bill> {
    return this.http.get<Bill>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch bill');
        return throwError(() => error);
      }),
    );
  }

  getUpcoming(): Observable<Bill[]> {
    return this.http.get<Bill[]>(`${this.apiUrl}/upcoming`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch upcoming bills');
        return throwError(() => error);
      }),
    );
  }

  create(bill: Bill): Observable<Bill> {
    return this.http.post<Bill>(this.apiUrl, bill).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to create bill');
        return throwError(() => error);
      }),
    );
  }

  markAsPaid(id: string): Observable<Bill> {
    return this.http.put<Bill>(`${this.apiUrl}/${id}/pay`, {}).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to mark bill as paid');
        return throwError(() => error);
      }),
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to delete bill');
        return throwError(() => error);
      }),
    );
  }
}
