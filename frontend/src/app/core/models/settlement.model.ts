export interface Settlement {
  id?: number | string;
  fromUserId?: number | string;
  fromUserName?: string;
  toUserId?: number | string;
  toUserName?: string;
  amount: number;
  isCompleted?: boolean;
  settlementDate?: Date | string;
  createdAt?: Date | string;
}

export interface SettlementBalance {
  userId?: number | string;
  userName?: string;
  userEmail?: string;
  balanceAmount?: number;
  totalOwed?: number;
  totalCredits?: number;
  balanceWithOthers?: { [userId: string]: number };
}

export interface SettlementSummary {
  totalSettlement: number;
  settlements: Settlement[];
  balanceSummary: SettlementBalance[];
}
