import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService, User } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  currentUser: User | null = null;
  loading = false;
  editMode = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.initForm();
  }

  initForm(): void {
    this.profileForm = this.fb.group({
      name: [this.currentUser?.name || '', [Validators.required, Validators.minLength(3)]],
      email: [{ value: this.currentUser?.email || '', disabled: true }],
      username: [this.currentUser?.username || '', [Validators.required]],
      phone: ['', [Validators.required, Validators.pattern(/^\d{10,15}$/)]],
    });
  }

  get f() {
    return this.profileForm.controls;
  }

  toggleEdit(): void {
    this.editMode = !this.editMode;
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      return;
    }

    this.loading = true;
    const updatedData = {
      name: this.profileForm.get('name')?.value,
      username: this.profileForm.get('username')?.value,
      phone: this.profileForm.get('phone')?.value,
    };

    this.authService.updateProfile(this.currentUser!.id!, updatedData).subscribe({
      next: (response) => {
        this.loading = false;
        this.editMode = false;
        this.notificationService.showSuccess('Profile updated successfully!');
      },
      error: (error) => {
        this.loading = false;
        this.notificationService.showError(error?.error?.message || 'Failed to update profile');
      },
    });
  }
}
