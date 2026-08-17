import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
})
export class SettingsComponent implements OnInit {
  userForm: FormGroup;
  loading = false;
  currentUser: User | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
  ) {
    this.userForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      phone: [''],
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    if (this.currentUser) {
      this.userForm.patchValue({
        name: this.currentUser.name,
        email: this.currentUser.email,
        username: this.currentUser.username,
        phone: this.currentUser.phone || '',
      });
    }
  }

  get f() {
    return this.userForm.controls;
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      return;
    }

    this.loading = true;
    const userData = this.userForm.value;

    if (this.currentUser?.id) {
      this.authService.updateProfile(this.currentUser.id, userData).subscribe({
        next: () => {
          this.loading = false;
          this.notificationService.showSuccess('Profile updated successfully!');
        },
        error: () => {
          this.loading = false;
          this.notificationService.showError('Failed to update profile');
        },
      });
    }
  }
}
