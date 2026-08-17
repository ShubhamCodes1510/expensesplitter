import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-balance-card',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './balance-card.component.html',
  styleUrls: ['./balance-card.component.css'],
})
export class BalanceCardComponent {
  @Input() title: string = '';
  @Input() amount: number = 0;
  @Input() icon: string = 'account_balance_wallet';
  @Input() cardClass: string = 'primary';
}
