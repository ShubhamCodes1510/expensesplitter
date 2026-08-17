import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-settlement-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './settlement-item.component.html',
  styleUrls: ['./settlement-item.component.css'],
})
export class SettlementItemComponent {
  @Input() settlement: any;
}
