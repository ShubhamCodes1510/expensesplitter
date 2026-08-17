import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BalancesComponent } from './balances/balances.component';
import { CalculateComponent } from './calculate/calculate.component';
import { HistoryComponent } from './history/history.component';

const routes: Routes = [
  { path: '', component: BalancesComponent },
  { path: 'calculate', component: CalculateComponent },
  { path: 'history', component: HistoryComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SettlementsRoutingModule { }
