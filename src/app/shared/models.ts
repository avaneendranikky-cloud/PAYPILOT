export interface ApiMessage { message?: string; }

export interface LoginPayload { userId: string; password: string; }
export interface RegisterPayload {
  userId: string; email: string; panNumber: string; bankAccountNumber: string;
  ifscCode: string; bankingPartner: string; password: string; confirmPassword: string;
}
export interface SessionUser { userId: string; email?: string; }

export interface Bill {
  billId?: number; userId: number; billName: string; billCategory: string;
  billDate: string; dueDate: string; amount: number; reminderFrequency?: string;
  notes?: string; recurringBill?: string; status?: string; snoozedUntil?: string;
}
export interface BillPayment {
  paymentId?: number; billId?: number; paymentAmount: number; paymentMethod: string;
  paymentDate?: string; paymentStatus?: string; transactionReference?: string;
}
export interface BillReminder {
  reminderId?: number; billId: number; reminderFrequency: string; startDate: string;
  message?: string; notificationType: string; recurringBill: string;
}
export interface ScheduledPayment {
  scheduledPaymentId?: number; billId: number; paymentDate: string;
  amountFrequency: string; active: boolean;
}
export interface PaymentStatus {
  paymentId: number; billName: string; billCategory: string; totalAmount: number;
  amountPaid: number; amountRemaining: number; feeAmount: number; dueDate: string;
  daysOverdue: number; status: string; progressPercent: number;
}
export interface PaymentHistory {
  paymentId: number; billName: string; billCategory: string; amountPaid: number;
  dueDate: string; paidAt: string; paymentMethod: string;
}
export interface TrackingResponse<T> {
  userId: string; category?: string; fromDate: string; toDate: string;
  resultCount: number; payments: T[];
}
