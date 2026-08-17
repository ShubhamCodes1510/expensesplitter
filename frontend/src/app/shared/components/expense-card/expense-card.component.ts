import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { Expense } from '../../../core/models/expense.model';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-expense-card',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule],
  templateUrl: './expense-card.component.html',
  styleUrls: ['./expense-card.component.css'],
})
export class ExpenseCardComponent {
  @Input() expense!: Expense;

  getPaidByName(): string {
    if (!this.expense) return 'Unknown';
    if (this.expense.paidByName) {
      return this.expense.paidByName;
    }
    if (typeof this.expense.paidBy === 'object' && 'name' in this.expense.paidBy) {
      return (this.expense.paidBy as User).name;
    }
    if (typeof this.expense.paidBy === 'string') {
      return this.expense.paidBy;
    }
    return 'Unknown';
  }

  getSplitCount(): number {
    return this.expense.splitBetween?.length || 1;
  }
}
