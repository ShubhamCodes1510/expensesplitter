import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ExpensesListComponent } from './expenses-list/expenses-list.component';
import { AddExpenseComponent } from './add-expense/add-expense.component';
import { ExpenseDetailComponent } from './expense-detail/expense-detail.component';

const routes: Routes = [
  { path: '', component: ExpensesListComponent },
  { path: 'add', component: AddExpenseComponent },
  { path: ':id', component: ExpenseDetailComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ExpensesRoutingModule { }
