import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface QuickAction {
  id: string;
  icon: string;
  label: string;
  action: () => void;
}

@Component({
  selector: 'app-quick-action',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quick-action.component.html',
  styleUrls: ['./quick-action.component.css'],
})
export class QuickActionComponent {
  @Input() actions: QuickAction[] = [];
}
