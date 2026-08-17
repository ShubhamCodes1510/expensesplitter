import { User } from './user.model';

export interface ExpenseShare {
  id?: number | string;
  user: User | string; // Can be User object or id string
  shareAmount: number;
  settlementStatus?: 'pending' | 'paid';
  settledAt?: Date | string;
}
