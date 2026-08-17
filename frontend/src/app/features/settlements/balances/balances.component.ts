import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatTabsModule } from '@angular/material/tabs';
import { tap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { SettlementsService } from '../../../core/services/settlements.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { SettlementBalance, Settlement } from '../../../core/models/settlement.model';

@Component({
  selector: 'app-balances',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatTabsModule,
  ],
  templateUrl: './balances.component.html',
  styleUrls: ['./balances.component.css'],
})
export class BalancesComponent implements OnInit {
  Math = Math;
  balances: SettlementBalance[] = [];
  settlements: Settlement[] = [];
  history: Settlement[] = [];
  loading = true;
  optimizeLoading = false;
  historyLoading = false;
  currentUserId: string | number | null = null;
  youOweBalances: SettlementBalance[] = [];
  youAreOwedBalances: SettlementBalance[] = [];
  totalYouOwe = 0;
  totalYouAreOwed = 0;

  constructor(
    private settlementsService: SettlementsService,
    private authService: AuthService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUserValue;
    this.currentUserId = user?.id || null;
    this.loadBalances();
  }

  onTabChange(index: number): void {
    if (index === 1 && this.settlements.length === 0) {
      this.loadSettlements();
    } else if (index === 2 && this.history.length === 0) {
      this.loadHistory();
    }
  }

  loadBalances(): void {
    this.loading = true;
    this.settlementsService
      .getBalances()
      .pipe(
        tap((balances: SettlementBalance[]) => {
          this.balances = balances;
          this.categorizeBalances();
          this.loading = false;
        }),
        catchError((error) => {
          console.error('Error loading balances:', error);
          this.notificationService.showError('Failed to load balances');
          this.loading = false;
          return of([]);
        }),
      )
      .subscribe();
  }

  loadSettlements(): void {
    this.optimizeLoading = true;
    this.settlementsService
      .calculateSettlements()
      .pipe(
        tap((settlements: Settlement[]) => {
          this.settlements = settlements;
          this.optimizeLoading = false;
        }),
        catchError((error) => {
          console.error('Error calculating settlements:', error);
          this.notificationService.showError('Failed to calculate settlements');
          this.optimizeLoading = false;
          return of([]);
        }),
      )
      .subscribe();
  }

  loadHistory(): void {
    this.historyLoading = true;
    this.settlementsService
      .getHistory()
      .pipe(
        tap((history: Settlement[]) => {
          this.history = history;
          this.historyLoading = false;
        }),
        catchError((error) => {
          console.error('Error loading history:', error);
          this.notificationService.showError('Failed to load settlement history');
          this.historyLoading = false;
          return of([]);
        }),
      )
      .subscribe();
  }

  categorizeBalances(): void {
    const currentId = this.currentUserId != null ? Number(this.currentUserId) : null;

    this.youOweBalances = [];
    this.youAreOwedBalances = [];
    this.totalYouOwe = 0;
    this.totalYouAreOwed = 0;

    for (const b of this.balances) {
      const amount = b.balanceAmount || 0;
      const numUserId = b.userId != null ? Number(b.userId) : null;

      if (numUserId === currentId) continue;
      if (amount === 0) continue;

      if (amount < 0) {
        this.youOweBalances.push(b);
        this.totalYouOwe += Math.abs(amount);
      } else {
        this.youAreOwedBalances.push(b);
        this.totalYouAreOwed += amount;
      }
    }
  }

  processPayment(balance: SettlementBalance): void {
    const amount = Math.abs(balance.balanceAmount || 0);
    if (this.currentUserId && balance.userId && amount > 0) {
      this.settlementsService
        .processPayment(String(this.currentUserId), String(balance.userId), amount)
        .subscribe({
          next: () => {
            this.notificationService.showSuccess(`Payment of ₹${amount.toFixed(2)} sent to ${balance.userName}`);
            this.loadBalances();
          },
          error: (error: any) => {
            console.error('Error processing payment:', error);
            this.notificationService.showError('Failed to process payment');
          },
        });
    }
  }

  settleOptimized(settlement: Settlement): void {
    if (settlement.fromUserId && settlement.toUserId && settlement.amount) {
      this.settlementsService
        .processPayment(String(settlement.fromUserId), String(settlement.toUserId), settlement.amount)
        .subscribe({
          next: () => {
            this.notificationService.showSuccess(`Settled ₹${settlement.amount.toFixed(2)} from ${settlement.fromUserName} to ${settlement.toUserName}`);
            this.loadSettlements();
            this.loadBalances();
          },
          error: (error: any) => {
            console.error('Error settling:', error);
            this.notificationService.showError('Failed to settle');
          },
        });
    }
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name
      .split(' ')
      .map((n) => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }

  isCurrentUser(userId: number | string | undefined): boolean {
    return userId != null && this.currentUserId != null && Number(userId) === Number(this.currentUserId);
  }

  formatDate(date: Date | string | undefined): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
  }

  formatDateTime(date: Date | string | undefined): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }

  refreshAll(): void {
    this.loadBalances();
    this.settlements = [];
    this.history = [];
  }
}
