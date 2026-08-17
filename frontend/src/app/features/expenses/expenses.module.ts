import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpensesRoutingModule } from './expenses-routing.module';
import { AddExpenseComponent } from './add-expense/add-expense.component';
import { ExpenseDetailComponent } from './expense-detail/expense-detail.component';
import { ExpensesListComponent } from './expenses-list/expenses-list.component';

@NgModule({
  imports: [
    CommonModule,
    ExpensesRoutingModule,
    AddExpenseComponent,
    ExpenseDetailComponent,
    ExpensesListComponent,
  ],
})
export class ExpensesModule {}
