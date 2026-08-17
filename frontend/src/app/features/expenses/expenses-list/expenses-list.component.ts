import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatMenuModule } from '@angular/material/menu';
import { Router } from '@angular/router';
import { ExpenseService } from '../../../core/services/expense.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Expense } from '../../../core/models/expense.model';

@Component({
  selector: 'app-expenses-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatMenuModule,
  ],
  templateUrl: './expenses-list.component.html',
  styleUrls: ['./expenses-list.component.css'],
})
export class ExpensesListComponent implements OnInit {
  expenses: Expense[] = [];
  filteredExpenses: Expense[] = [];
  loading = true;
  searchTerm = '';
  displayedColumns: string[] = ['description', 'amount', 'paidBy', 'date', 'actions'];

  constructor(
    private expenseService: ExpenseService,
    private notificationService: NotificationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadExpenses();
  }

  loadExpenses(): void {
    this.loading = true;
    this.expenseService.getAll().subscribe({
      next: (expenses: Expense[]) => {
        this.expenses = expenses;
        this.filteredExpenses = expenses;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading expenses:', error);
        this.notificationService.showError('Failed to load expenses');
        this.loading = false;
      },
    });
  }

  onSearch(event: any): void {
    this.searchTerm = event.target.value.toLowerCase();
    this.filterExpenses();
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filterExpenses();
  }

  filterExpenses(): void {
    if (!this.searchTerm) {
      this.filteredExpenses = [...this.expenses];
    } else {
      this.filteredExpenses = this.expenses.filter((expense) =>
        expense.description.toLowerCase().includes(this.searchTerm),
      );
    }
  }

  onViewExpense(expenseId: string | number): void {
    this.router.navigate(['/expenses', expenseId]);
  }

  onDeleteExpense(expenseId: string | number): void {
    if (confirm('Are you sure you want to delete this expense?')) {
      this.expenseService.delete(String(expenseId)).subscribe({
        next: () => {
          this.notificationService.showSuccess('Expense deleted successfully');
          this.loadExpenses();
        },
        error: (error) => {
          console.error('Error deleting expense:', error);
          this.notificationService.showError('Failed to delete expense');
        },
      });
    }
  }

  onAddExpense(): void {
    this.router.navigate(['/expenses/add']);
  }

  getPaidByName(element: any): string {
    if (element?.paidByName) {
      return element.paidByName;
    }
    if (element?.paidBy?.name) {
      return element.paidBy.name;
    }
    if (typeof element?.paidBy === 'string') {
      return element.paidBy;
    }
    return 'Unknown';
  }
}
