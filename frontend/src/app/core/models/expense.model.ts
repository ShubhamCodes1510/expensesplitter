import { User } from './user.model';

export interface Category {
  id?: number | string;
  name: string;
  description?: string;
  icon?: string;
}

export interface Expense {
  id?: number | string;
  description: string;
  amount: number;
  paidBy: User | string | { id: number | string };
  paidById?: number | string;
  paidByName?: string;
  groupId?: number | string;
  category?: Category;
  splitBetween?: (User | string)[];
  splitType?: 'EQUAL' | 'CUSTOM' | 'PERCENTAGE' | 'SHARES';
  date?: Date | string;
  receipts?: Receipt[];
  receiptUrl?: string;
  shares?: ExpenseShare[];
  createdAt?: Date | string;
  updatedAt?: Date | string;
}

export interface ExpenseShare {
  id?: number | string;
  expenseId?: number | string;
  user: { id: number | string; name?: string };
  userId?: number | string;
  userName?: string;
  shareAmount: number;
  settled: boolean;
}

export interface Receipt {
  id?: number | string;
  fileName: string;
  filePath: string;
  uploadedAt?: Date | string;
}
