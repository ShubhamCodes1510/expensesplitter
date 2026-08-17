import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ExpenseService } from '../../../core/services/expense.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Expense } from '../../../core/models/expense.model';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-expense-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatTableModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDialogModule,
  ],
  templateUrl: './expense-detail.component.html',
  styleUrls: ['./expense-detail.component.scss'],
})
export class ExpenseDetailComponent implements OnInit {
  expense: Expense | null = null;
  loading = true;
  displayedColumns: string[] = ['participant', 'amount', 'status'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private expenseService: ExpenseService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const expenseId = params['id'];
      if (expenseId) {
        this.loadExpense(expenseId);
      }
    });
  }

  loadExpense(id: string): void {
    this.loading = true;
    this.expenseService.getById(id).subscribe({
      next: (expense) => {
        this.expense = expense;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        console.error('Error loading expense:', error);
        this.notificationService.showError('Failed to load expense details');
        this.router.navigate(['/expenses']);
      },
    });
  }

  deleteExpense(): void {
    if (!this.expense?.id) return;

    const confirm = window.confirm('Are you sure you want to delete this expense?');
    if (confirm) {
      this.expenseService.delete(this.expense.id.toString()).subscribe({
        next: () => {
          this.notificationService.showSuccess('Expense deleted successfully');
          this.router.navigate(['/expenses']);
        },
        error: (error) => {
          console.error('Error deleting expense:', error);
          this.notificationService.showError('Failed to delete expense');
        },
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/expenses']);
  }

  getParticipantName(participant: any): string {
    if (participant?.userName) {
      return participant.userName;
    }
    if (participant?.user?.name) {
      return participant.user.name;
    }
    if (participant?.user?.id) {
      return 'User ' + participant.user.id;
    }
    return 'Unknown';
  }

  getPaidByName(paidBy: string | any): string {
    if (typeof paidBy === 'string') {
      return paidBy;
    }
    if (this.expense?.paidByName) {
      return this.expense.paidByName;
    }
    return paidBy?.name || 'Unknown';
  }
}
