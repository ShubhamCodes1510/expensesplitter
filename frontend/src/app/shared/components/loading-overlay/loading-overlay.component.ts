import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LoadingService } from '../../../core/services/loading.service';

@Component({
  selector: 'app-loading-overlay',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule],
  template: `
    <div class="loading-overlay" *ngIf="loadingService.isLoading">
      <div class="spinner-container">
        <mat-spinner diameter="50"></mat-spinner>
        <p>Loading...</p>
      </div>
    </div>
  `,
  styles: [`
    .loading-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(15, 23, 42, 0.85);
      backdrop-filter: blur(8px);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: var(--z-index-modal);
      animation: fadeIn 0.2s ease-out;
    }
    
    .spinner-container {
      background: var(--color-surface-elevated);
      padding: var(--spacing-3xl);
      border-radius: var(--radius-2xl);
      text-align: center;
      box-shadow: var(--shadow-2xl);
      border: 1px solid var(--color-border);
      min-width: 200px;
      animation: slideUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
    }
    
    .spinner-container p {
      margin-top: var(--spacing-lg);
      color: var(--color-text-secondary);
      font-size: var(--font-size-sm);
      font-weight: var(--font-weight-medium);
      letter-spacing: var(--letter-spacing-wide);
    }
    
    ::ng-deep .mat-mdc-progress-spinner {
      --mdc-circular-progress-active-indicator-color: var(--color-primary) !important;
    }
    
    @keyframes fadeIn {
      from {
        opacity: 0;
      }
      to {
        opacity: 1;
      }
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
    
    /* Dark theme adjustments */
    :host-context([data-theme="dark"]) .loading-overlay,
    :host-context(.dark-theme) .loading-overlay {
      background: rgba(0, 0, 0, 0.75);
    }
    
    :host-context([data-theme="dark"]) .spinner-container,
    :host-context(.dark-theme) .spinner-container {
      background: var(--color-surface);
      border-color: var(--color-border);
    }
  `]
})
export class LoadingOverlayComponent {
  constructor(public loadingService: LoadingService) {}
}
