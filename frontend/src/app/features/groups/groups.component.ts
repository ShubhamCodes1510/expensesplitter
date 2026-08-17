import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { GroupService, Group } from '../../core/services/group.service';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-groups',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatChipsModule,
  ],
  template: `
    <div class="groups-page">
      <div class="page-header">
        <h1>Groups</h1>
        <div class="header-actions">
          <button class="btn btn-secondary" (click)="openJoinModal()">
            <mat-icon>group_add</mat-icon>
            Join Group
          </button>
          <button class="btn btn-primary" (click)="openCreateModal()">
            <mat-icon>add</mat-icon>
            Create Group
          </button>
        </div>
      </div>

      <!-- Create Group Modal -->
      <div class="modal-overlay" *ngIf="showCreateModal" (click)="closeModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Create New Group</h2>
            <button class="close-btn" (click)="closeModal()">
              <mat-icon>close</mat-icon>
            </button>
          </div>
          <form [formGroup]="groupForm" (ngSubmit)="createGroup()">
            <div class="form-group">
              <label class="form-label">Group Name</label>
              <input type="text" class="form-input" formControlName="name" placeholder="Enter group name">
            </div>
            <div class="form-group">
              <label class="form-label">Description (Optional)</label>
              <textarea class="form-input" formControlName="description" placeholder="Enter description" rows="3"></textarea>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="loading || groupForm.invalid">
                <mat-spinner *ngIf="loading" diameter="20"></mat-spinner>
                <span *ngIf="!loading">Create</span>
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Join Group Modal -->
      <div class="modal-overlay" *ngIf="showJoinModal" (click)="closeModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Join Group</h2>
            <button class="close-btn" (click)="closeModal()">
              <mat-icon>close</mat-icon>
            </button>
          </div>
          <form [formGroup]="joinForm" (ngSubmit)="joinGroup()">
            <div class="form-group">
              <label class="form-label">Invite Code</label>
              <input type="text" class="form-input" formControlName="inviteCode" placeholder="Enter invite code">
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="loading || joinForm.invalid">
                <mat-spinner *ngIf="loading" diameter="20"></mat-spinner>
                <span *ngIf="!loading">Join</span>
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Add Members Modal -->
      <div class="modal-overlay" *ngIf="showAddMembersModal" (click)="closeModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Add Members</h2>
            <button class="close-btn" (click)="closeModal()">
              <mat-icon>close</mat-icon>
            </button>
          </div>
          <form [formGroup]="addMemberForm" (ngSubmit)="addMember()">
            <div class="form-group">
              <label class="form-label">Member Username</label>
              <input type="text" class="form-input" formControlName="username" placeholder="Enter username to add">
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="loading || addMemberForm.invalid">
                <mat-spinner *ngIf="loading" diameter="20"></mat-spinner>
                <span *ngIf="!loading">Add Member</span>
              </button>
            </div>
          </form>
          <div class="members-list" *ngIf="selectedGroup?.members?.length">
            <h4>Current Members ({{ selectedGroup?.members?.length }})</h4>
            <div class="member-chip" *ngFor="let member of selectedGroup?.members">
              <mat-icon>person</mat-icon>
              <span>{{ member?.name }}</span>
              <span class="username">({{ '@' }}{{ member?.username }})</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Edit Group Modal -->
      <div class="modal-overlay" *ngIf="showEditModal" (click)="closeModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h2>Edit Group</h2>
            <button class="close-btn" (click)="closeModal()">
              <mat-icon>close</mat-icon>
            </button>
          </div>
          <form [formGroup]="editForm" (ngSubmit)="updateGroup()">
            <div class="form-group">
              <label class="form-label">Group Name</label>
              <input type="text" class="form-input" formControlName="name" placeholder="Enter group name">
            </div>
            <div class="form-group">
              <label class="form-label">Description (Optional)</label>
              <textarea class="form-input" formControlName="description" placeholder="Enter description" rows="3"></textarea>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" (click)="closeModal()">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="loading || editForm.invalid">
                <mat-spinner *ngIf="loading" diameter="20"></mat-spinner>
                <span *ngIf="!loading">Save Changes</span>
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Groups List -->
      <div class="groups-grid" *ngIf="!loading && groups.length > 0">
        <div class="group-card" *ngFor="let group of groups">
          <div class="group-header">
            <div class="group-icon">
              <mat-icon>group</mat-icon>
            </div>
            <div class="group-actions-card">
              <button class="icon-btn" (click)="openAddMembersModal(group)" title="Add Members">
                <mat-icon>person_add</mat-icon>
              </button>
              <button class="icon-btn" (click)="openEditModal(group)" title="Edit Group">
                <mat-icon>edit</mat-icon>
              </button>
            </div>
          </div>
          <div class="group-info">
            <h3>{{ group.name }}</h3>
            <p *ngIf="group.description">{{ group.description }}</p>
            <span class="member-count">
              <mat-icon>people</mat-icon>
              {{ group.memberCount }} members
            </span>
          </div>
          <div class="members-preview" *ngIf="group.members?.length">
            <div class="member-avatars">
              <div class="avatar" *ngFor="let member of group.members?.slice(0, 3)" [title]="member.name">
                {{ getInitials(member.name) }}
              </div>
              <div class="avatar more" *ngIf="(group.members?.length || 0) > 3">
                +{{ (group.members?.length || 0) - 3 }}
              </div>
            </div>
            <div class="member-names">
              <span *ngFor="let member of group.members?.slice(0, 3)">{{ member.name }}</span>
              <span *ngIf="(group.members?.length || 0) > 3"> and {{ (group.members?.length || 0) - 3 }} more</span>
            </div>
          </div>
          <div class="group-actions">
            <span class="invite-code">Code: {{ group.inviteCode }}</span>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div class="empty-state" *ngIf="!loading && groups.length === 0">
        <mat-icon>group_add</mat-icon>
        <h3>No groups yet</h3>
        <p>Create a new group or join an existing one</p>
        <div class="empty-actions">
          <button class="btn btn-primary" (click)="openCreateModal()">
            <mat-icon>add</mat-icon>
            Create Group
          </button>
          <button class="btn btn-secondary" (click)="openJoinModal()">
            <mat-icon>group_add</mat-icon>
            Join Group
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .groups-page {
      max-width: var(--content-max-width, 1200px);
      padding: var(--spacing-lg);
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--spacing-xl);
    }

    .page-header h1 {
      font-size: var(--font-size-2xl);
      font-weight: 700;
      color: var(--color-text-primary);
    }

    .header-actions {
      display: flex;
      gap: var(--spacing-sm);
    }

    .groups-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: var(--spacing-lg);
    }

    .group-card {
      display: flex;
      flex-direction: column;
      background: var(--color-surface);
      border-radius: var(--radius-lg);
      padding: var(--spacing-lg);
      border: 1px solid var(--color-border-light);
      transition: all var(--transition-normal);
    }

    .group-card:hover {
      box-shadow: var(--shadow-md);
      transform: translateY(-2px);
    }

    .group-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
    }

    .group-icon {
      width: 56px;
      height: 56px;
      border-radius: var(--radius-lg);
      background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: var(--spacing-md);
    }

    .group-icon mat-icon {
      font-size: 28px;
      width: 28px;
      height: 28px;
      color: white;
    }

    .group-actions-card {
      display: flex;
      gap: 4px;
    }

    .icon-btn {
      background: var(--color-surface);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-md);
      padding: 6px;
      cursor: pointer;
      color: var(--color-text-secondary);
      transition: all var(--transition-fast);
    }

    .icon-btn:hover {
      background: var(--color-primary);
      color: white;
      border-color: var(--color-primary);
    }

    .icon-btn mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
    }

    .group-info h3 {
      font-size: var(--font-size-lg);
      font-weight: 600;
      color: var(--color-text-primary);
      margin: 0 0 4px 0;
    }

    .group-info p {
      font-size: var(--font-size-sm);
      color: var(--color-text-secondary);
      margin: 0 0 8px 0;
    }

    .member-count {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: var(--font-size-xs);
      color: var(--color-text-muted);
    }

    .member-count mat-icon {
      font-size: 14px;
      width: 14px;
      height: 14px;
    }

    .members-preview {
      margin-top: var(--spacing-md);
      padding-top: var(--spacing-md);
      border-top: 1px solid var(--color-border-light);
    }

    .member-avatars {
      display: flex;
      margin-bottom: 6px;
    }

    .avatar {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: var(--color-primary);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 10px;
      font-weight: 600;
      margin-left: -8px;
      border: 2px solid var(--color-surface);
    }

    .avatar:first-child {
      margin-left: 0;
    }

    .avatar.more {
      background: var(--color-text-muted);
    }

    .member-names {
      font-size: var(--font-size-xs);
      color: var(--color-text-secondary);
    }

    .group-actions {
      margin-top: var(--spacing-md);
      padding-top: var(--spacing-md);
      border-top: 1px solid var(--color-border-light);
    }

    .invite-code {
      font-size: var(--font-size-xs);
      color: var(--color-text-muted);
      font-family: monospace;
      background: var(--color-background);
      padding: 4px 8px;
      border-radius: var(--radius-sm);
    }

    /* Modal Styles */
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      animation: fadeIn 0.2s ease;
    }

    .modal-content {
      background: var(--color-surface);
      border-radius: var(--radius-xl);
      padding: var(--spacing-xl);
      width: 100%;
      max-width: 450px;
      animation: slideUp 0.3s ease;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--spacing-lg);
    }

    .modal-header h2 {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--color-text-primary);
      margin: 0;
    }

    .close-btn {
      background: none;
      border: none;
      cursor: pointer;
      color: var(--color-text-muted);
      padding: 4px;
      border-radius: var(--radius-sm);
      transition: all var(--transition-fast);
    }

    .close-btn:hover {
      background: var(--color-surface-hover);
      color: var(--color-text-primary);
    }

    .form-group {
      margin-bottom: var(--spacing-md);
    }

    .form-label {
      display: block;
      font-size: var(--font-size-sm);
      font-weight: 500;
      color: var(--color-text-primary);
      margin-bottom: var(--spacing-xs);
    }

    .form-input {
      width: 100%;
      padding: var(--spacing-sm) var(--spacing-md);
      font-family: var(--font-family);
      font-size: var(--font-size-sm);
      color: var(--color-text-primary);
      background: var(--color-surface);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-md);
      transition: all var(--transition-fast);
    }

    .form-input:focus {
      outline: none;
      border-color: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
    }

    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: var(--spacing-sm);
      margin-top: var(--spacing-lg);
    }

    .members-list {
      margin-top: var(--spacing-lg);
      padding-top: var(--spacing-lg);
      border-top: 1px solid var(--color-border-light);
    }

    .members-list h4 {
      font-size: var(--font-size-sm);
      font-weight: 600;
      color: var(--color-text-primary);
      margin: 0 0 var(--spacing-md) 0;
    }

    .member-chip {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      background: var(--color-background);
      border-radius: var(--radius-md);
      margin-bottom: 8px;
    }

    .member-chip mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
      color: var(--color-primary);
    }

    .member-chip .username {
      color: var(--color-text-muted);
      font-size: var(--font-size-xs);
    }

    /* Empty State */
    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: var(--spacing-xl);
      text-align: center;
      background: var(--color-surface);
      border-radius: var(--radius-xl);
      border: 2px dashed var(--color-border);
    }

    .empty-state mat-icon {
      font-size: 64px;
      width: 64px;
      height: 64px;
      color: var(--color-text-muted);
      margin-bottom: var(--spacing-md);
    }

    .empty-state h3 {
      font-size: var(--font-size-lg);
      font-weight: 600;
      color: var(--color-text-primary);
      margin: 0 0 var(--spacing-xs) 0;
    }

    .empty-state p {
      color: var(--color-text-muted);
      margin: 0 0 var(--spacing-lg) 0;
    }

    .empty-actions {
      display: flex;
      gap: var(--spacing-sm);
    }

    .btn {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 10px 16px;
      font-size: var(--font-size-sm);
      font-weight: 500;
      border-radius: var(--radius-md);
      cursor: pointer;
      transition: all var(--transition-fast);
      border: none;
    }

    .btn-primary {
      background: var(--color-primary);
      color: white;
    }

    .btn-primary:hover {
      background: var(--color-primary-dark);
    }

    .btn-secondary {
      background: var(--color-surface);
      color: var(--color-text-primary);
      border: 1px solid var(--color-border);
    }

    .btn-secondary:hover {
      background: var(--color-surface-hover);
    }

    .btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  `]
})
export class GroupsComponent implements OnInit {
  groups: Group[] = [];
  loading = false;
  showCreateModal = false;
  showJoinModal = false;
  showAddMembersModal = false;
  showEditModal = false;
  selectedGroup: Group | null = null;
  groupForm!: FormGroup;
  joinForm!: FormGroup;
  addMemberForm!: FormGroup;
  editForm!: FormGroup;

  constructor(
    private groupService: GroupService,
    private authService: AuthService,
    private notificationService: NotificationService,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.initForms();
    this.loadGroups();
  }

  initForms(): void {
    this.groupForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
    });

    this.joinForm = this.fb.group({
      inviteCode: ['', [Validators.required]],
    });

    this.addMemberForm = this.fb.group({
      username: ['', [Validators.required]],
    });

    this.editForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
    });
  }

  loadGroups(): void {
    const user = this.authService.currentUserValue;
    if (!user) return;

    this.loading = true;
    this.groupService.getUserGroups(Number(user.id)).subscribe({
      next: (groups) => {
        this.groups = groups;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.notificationService.showError('Failed to load groups');
      },
    });
  }

  openCreateModal(): void {
    this.showCreateModal = true;
  }

  openJoinModal(): void {
    this.showJoinModal = true;
  }

  openAddMembersModal(group: Group): void {
    this.selectedGroup = group;
    this.showAddMembersModal = true;
  }

  openEditModal(group: Group): void {
    this.selectedGroup = group;
    this.editForm.patchValue({
      name: group.name,
      description: group.description || '',
    });
    this.showEditModal = true;
  }

  closeModal(): void {
    this.showCreateModal = false;
    this.showJoinModal = false;
    this.showAddMembersModal = false;
    this.showEditModal = false;
    this.selectedGroup = null;
    this.groupForm.reset();
    this.joinForm.reset();
    this.addMemberForm.reset();
    this.editForm.reset();
  }

  createGroup(): void {
    if (this.groupForm.invalid) return;

    const user = this.authService.currentUserValue;
    if (!user) return;

    this.loading = true;
    const { name, description } = this.groupForm.value;

    this.groupService.createGroup({ name, description }, Number(user.id)).subscribe({
      next: (group) => {
        this.groups.unshift(group);
        this.loading = false;
        this.closeModal();
        this.notificationService.showSuccess('Group created successfully!');
      },
      error: () => {
        this.loading = false;
        this.notificationService.showError('Failed to create group');
      },
    });
  }

  joinGroup(): void {
    if (this.joinForm.invalid) return;

    const user = this.authService.currentUserValue;
    if (!user) return;

    this.loading = true;
    const { inviteCode } = this.joinForm.value;

    this.groupService.joinGroup(inviteCode, Number(user.id)).subscribe({
      next: (group) => {
        const exists = this.groups.find(g => g.id === group.id);
        if (!exists) {
          this.groups.unshift(group);
        }
        this.loading = false;
        this.closeModal();
        this.notificationService.showSuccess('Joined group successfully!');
      },
      error: () => {
        this.loading = false;
        this.notificationService.showError('Failed to join group. Check invite code.');
      },
    });
  }

  addMember(): void {
    if (this.addMemberForm.invalid || !this.selectedGroup) return;

    this.loading = true;
    const { username } = this.addMemberForm.value;

    this.groupService.addMemberByUsername(Number(this.selectedGroup.id), username).subscribe({
      next: (group) => {
        const index = this.groups.findIndex(g => g.id === group.id);
        if (index !== -1) {
          this.groups[index] = group;
        }
        this.selectedGroup = group;
        this.loading = false;
        this.addMemberForm.reset();
        this.notificationService.showSuccess('Member added successfully!');
      },
      error: (err) => {
        this.loading = false;
        this.notificationService.showError(err.error?.message || 'Failed to add member. User may not exist.');
      },
    });
  }

  updateGroup(): void {
    if (this.editForm.invalid || !this.selectedGroup) return;

    this.loading = true;
    const { name, description } = this.editForm.value;

    this.groupService.updateGroup(Number(this.selectedGroup.id), { name, description }).subscribe({
      next: (group) => {
        const index = this.groups.findIndex(g => g.id === group.id);
        if (index !== -1) {
          this.groups[index] = group;
        }
        this.loading = false;
        this.closeModal();
        this.notificationService.showSuccess('Group updated successfully!');
      },
      error: () => {
        this.loading = false;
        this.notificationService.showError('Failed to update group');
      },
    });
  }

  getInitials(name: string): string {
    if (!name) return '?';
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }
}
