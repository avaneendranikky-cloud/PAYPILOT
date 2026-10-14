import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiMessage, Bill, BillPayment, BillReminder, LoginPayload, PaymentHistory, PaymentStatus, RegisterPayload, ScheduledPayment, TrackingResponse } from '../shared/models';

@Injectable({ providedIn: 'root' })
export class PaypilotApiService {
  constructor(private http: HttpClient) {}

  register(body: RegisterPayload): Observable<unknown> { return this.http.post(`${environment.authApi}/api/users/register`, body); }
  verifyRegistration(email: string, otp: string): Observable<ApiMessage> {
    return this.http.post<ApiMessage>(`${environment.authApi}/api/users/verify-registration-otp`, { email, otp });
  }
  login(body: LoginPayload): Observable<ApiMessage> { return this.http.post<ApiMessage>(`${environment.authApi}/api/users/login`, body); }
  forgotPassword(email: string): Observable<ApiMessage> { return this.http.post<ApiMessage>(`${environment.authApi}/api/users/forgot-password`, { email }); }
  verifyResetOtp(email: string, otp: string): Observable<ApiMessage> { return this.http.post<ApiMessage>(`${environment.authApi}/api/users/verify-otp`, { email, otp }); }
  resetPassword(email: string, newPassword: string, confirmPassword: string): Observable<ApiMessage> {
    return this.http.post<ApiMessage>(`${environment.authApi}/api/users/reset-password`, { email, newPassword, confirmPassword });
  }

  getBills(userId = environment.billUserId): Observable<Bill[]> { return this.http.get<Bill[]>(`${environment.billsApi}/api/bills/user/${userId}`); }
  createBill(body: Bill): Observable<Bill> { return this.http.post<Bill>(`${environment.billsApi}/api/bills`, body); }
  updateBill(id: number, body: Bill): Observable<Bill> { return this.http.put<Bill>(`${environment.billsApi}/api/bills/${id}`, body); }
  deleteBill(id: number): Observable<string> { return this.http.delete(`${environment.billsApi}/api/bills/${id}`, { responseType: 'text' }); }
  payBill(id: number, body: BillPayment): Observable<BillPayment> { return this.http.post<BillPayment>(`${environment.billsApi}/api/payments/bill/${id}`, body); }
  getBillPayments(id: number): Observable<BillPayment[]> { return this.http.get<BillPayment[]>(`${environment.billsApi}/api/payments/bill/${id}`); }
  snoozeBill(id: number, date: string): Observable<Bill> { return this.http.put<Bill>(`${environment.billsApi}/api/bills/${id}/snooze`, null, { params: { date } }); }
  createReminder(body: BillReminder): Observable<BillReminder> { return this.http.post<BillReminder>(`${environment.billsApi}/api/reminders`, body); }

  getSchedules(): Observable<ScheduledPayment[]> { return this.http.get<ScheduledPayment[]>(`${environment.scheduleApi}/api/scheduled-payments`); }
  createSchedule(body: ScheduledPayment): Observable<ScheduledPayment> { return this.http.post<ScheduledPayment>(`${environment.scheduleApi}/api/scheduled-payments`, body); }
  cancelSchedule(id: number): Observable<ScheduledPayment> { return this.http.put<ScheduledPayment>(`${environment.scheduleApi}/api/scheduled-payments/${id}/cancel`, null); }

  getTracking<T extends PaymentStatus | PaymentHistory>(userId: string, view: 'overview' | 'progress' | 'history', fromDate: string, toDate: string): Observable<TrackingResponse<T>> {
    const params = new HttpParams().set('fromDate', fromDate).set('toDate', toDate);
    return this.http.get<TrackingResponse<T>>(`${environment.trackingApi}/api/v1/users/${encodeURIComponent(userId)}/payments/${view}`, { params });
  }
}
