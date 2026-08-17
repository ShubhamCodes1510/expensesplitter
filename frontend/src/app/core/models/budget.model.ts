import { Category } from './category.model';
import { User } from './user.model';

export interface Budget {
  id?: number | string;
  userId?: number | string;
  userName?: string;
  category?: Category;
  categoryId?: number | string;
  categoryName?: string;
  amount: number;
  period: 'MONTHLY' | 'WEEKLY' | 'YEARLY' | 'CUSTOM';
  startDate?: Date | string;
  endDate?: Date | string;
  isActive?: boolean;
  description?: string;
  alertThreshold?: number;
  spentAmount?: number;
  remainingAmount?: number;
  percentageUsed?: number;
  createdAt?: Date | string;
  updatedAt?: Date | string;
}

export interface BudgetSummary {
  amount: number;
  spentAmount: number;
  remainingAmount: number;
  percentageUsed: number;
  period?: string;
}