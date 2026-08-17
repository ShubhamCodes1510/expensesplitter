import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileComponent } from './profile.component';
import { CategoriesComponent } from './categories/categories.component';
import { SettingsComponent } from './settings/settings.component';

const routes: Routes = [
  { path: '', component: ProfileComponent },
  { path: 'categories', component: CategoriesComponent },
  { path: 'settings', component: SettingsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProfileRoutingModule { }
