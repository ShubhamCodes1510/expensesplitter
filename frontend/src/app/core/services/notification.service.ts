import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly DURATION = 3500; // 3.5 seconds

  constructor(private snackBar: MatSnackBar) {}

  showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: this.DURATION,
      panelClass: ['snackbar-success'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: this.DURATION,
      panelClass: ['snackbar-error'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  showInfo(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: this.DURATION,
      panelClass: ['snackbar-info'],
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }
}
