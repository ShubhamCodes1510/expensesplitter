import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpProgressEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';
import { Receipt } from '../models/receipt.model';

export interface UploadResponse {
  url: string;
  fileName: string;
  size: number;
}

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  private apiUrl = `${environment.apiUrl}/files`;

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
  ) {}

  uploadReceipt(file: File, expenseId?: string): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('file', file);

    const endpoint = expenseId ? `${this.apiUrl}/upload/${expenseId}` : `${this.apiUrl}/upload`;
    return this.http.post<UploadResponse>(endpoint, formData).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to upload receipt');
        return throwError(() => error);
      }),
    );
  }

  uploadReceiptWithProgress(file: File, expenseId?: string): Observable<HttpEvent<UploadResponse>> {
    const formData = new FormData();
    formData.append('file', file);

    const endpoint = expenseId ? `${this.apiUrl}/upload/${expenseId}` : `${this.apiUrl}/upload`;
    return this.http
      .post<UploadResponse>(endpoint, formData, {
        reportProgress: true,
        observe: 'events',
      })
      .pipe(
        catchError((error) => {
          this.notificationService.showError('Failed to upload receipt');
          return throwError(() => error);
        }),
      );
  }

  deleteReceipt(fileId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${fileId}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to delete receipt');
        return throwError(() => error);
      }),
    );
  }

  getReceiptsByExpense(expenseId: string): Observable<Receipt[]> {
    return this.http.get<Receipt[]>(`${this.apiUrl}/expense/${expenseId}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch receipts');
        return throwError(() => error);
      }),
    );
  }

  getReceiptById(id: string): Observable<Receipt> {
    return this.http.get<Receipt>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        this.notificationService.showError('Failed to fetch receipt');
        return throwError(() => error);
      }),
    );
  }
}
