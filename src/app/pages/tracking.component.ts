import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PaypilotApiService } from '../core/paypilot-api.service';
import { SessionService } from '../core/session.service';
import { Bill, BillPayment, PaymentHistory, PaymentStatus } from '../shared/models';
import { environment } from '../../environments/environment';
import { apiErrorMessage } from '../core/error-message';
import { IconComponent } from '../shared/icon.component';
import { icons } from '../shared/icons';
import { riseIn } from '../shared/motion';

@Component({ selector: 'pp-tracking', standalone: true, imports: [CommonModule, CurrencyPipe, DatePipe, FormsModule, IconComponent], animations: [riseIn], template: `
  <div class="page-stack" [@riseIn]="true"><section class="page-title-row"><div><div class="eyebrow"><span></span> THE BIGGER PICTURE</div><h1>Payment <em>tracking</em></h1><p class="page-lede">Every payment has a story. Here’s yours, made easy to follow.</p></div><button class="button button-soft" (click)="load()"><pp-icon [icon]="icons.refresh" /> Refresh</button></section>
    <section class="tracking-filter panel"><div class="filter-copy"><span class="filter-icon"><pp-icon [icon]="icons.filter" /></span><div><strong>Choose a time range</strong><small>See how your payments are moving.</small></div></div><div class="date-filters"><label>From <input type="date" [(ngModel)]="fromDate"></label><label>To <input type="date" [(ngModel)]="toDate"></label><button class="button button-dark button-small" (click)="load()">Update view</button></div></section>
    <div class="alert alert-error" *ngIf="errorMessage">{{ errorMessage }}</div>
    <section class="summary-grid tracking-metrics"><article class="summary-card summary-card-dark"><div class="summary-top"><span>PAYMENTS FOUND</span><span class="summary-icon"><pp-icon [icon]="icons.chart" /></span></div><strong>{{ statuses.length | number:'2.0' }}</strong><div class="summary-foot"><span class="status-dot mint-dot"></span>In selected period</div></article><article class="summary-card"><div class="summary-top"><span>PAID IN FULL</span><span class="summary-icon icon-mint"><pp-icon [icon]="icons.check" /></span></div><strong>{{ paidCount | number:'2.0' }}</strong><div class="summary-foot">Completed payments</div></article><article class="summary-card"><div class="summary-top"><span>REMAINING</span><span class="summary-icon icon-lavender"><pp-icon [icon]="icons.wallet" /></span></div><strong>{{ totalRemaining | currency:'INR':'symbol':'1.0-0' }}</strong><div class="summary-foot">Across visible payments</div></article></section>
    <section class="panel table-panel"><div class="panel-heading"><div><div class="eyebrow"><span></span> PAYMENT PULSE</div><h2>Progress & history</h2></div><span class="live-pill">{{ fromDate | date:'MMM d' }} — {{ toDate | date:'MMM d' }}</span></div><div class="table-scroll"><table class="data-table"><thead><tr><th>PAYMENT</th><th>DUE DATE</th><th>PAID</th><th>REMAINING</th><th>PROGRESS</th><th>STATUS</th></tr></thead><tbody><tr *ngFor="let row of statuses"><td><div class="table-bill"><span class="bill-monogram lavender-bg">{{ row.billName.charAt(0) }}</span><span><b>{{ row.billName }}</b><small>{{ row.billCategory }}</small></span></div></td><td>{{ row.dueDate | date:'MMM d, y' }}</td><td>{{ row.amountPaid | currency:'INR':'symbol':'1.0-0' }}</td><td>{{ row.amountRemaining | currency:'INR':'symbol':'1.0-0' }}</td><td><div class="progress-cell"><div class="progress-track"><i [style.width.%]="row.progressPercent"></i></div><small>{{ row.progressPercent }}%</small></div></td><td><span class="status-badge" [ngClass]="row.status.toLowerCase()">{{ row.status.replace('_',' ') }}</span></td></tr><tr *ngIf="!loading && statuses.length===0"><td colspan="6"><div class="empty-state compact"><span class="empty-icon"><pp-icon [icon]="icons.chart" /></span><strong>No bills due in this period</strong><p>Try a wider date range or check that the bill is saved in your account.</p></div></td></tr></tbody></table></div></section>
    <section class="panel history-panel"><div class="panel-heading"><div><div class="eyebrow"><span></span> COMPLETED</div><h2>Payment history</h2></div></div><div class="history-list"><div class="history-row" *ngFor="let row of history"><span class="history-check"><pp-icon [icon]="icons.check" /></span><span class="history-name"><b>{{ row.billName }}</b><small>{{ row.billCategory }} · {{ row.paymentMethod || 'Payment' }} · {{ row.paidAt | date:'MMM d' }}</small></span><strong>{{ row.amountPaid | currency:'INR':'symbol':'1.0-0' }}</strong></div><div class="empty-inline" *ngIf="!loading && !history.length">No completed payments in this date range.</div></div></section>
  </div>` })
export class TrackingComponent implements OnInit {
  icons = icons; statuses: PaymentStatus[] = []; history: PaymentHistory[] = []; loading = false; errorMessage = '';
  fromDate = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().slice(0, 10);
  toDate = new Date().toISOString().slice(0, 10);
  constructor(private api: PaypilotApiService, private session: SessionService) {}
  ngOnInit(): void { this.load(); }
  get userId(): string { return this.session.user?.userId || ''; }
  get paidCount(): number { return this.statuses.filter(x => x.status === 'PAID').length; }
  get totalRemaining(): number { return this.statuses.reduce((sum, row) => sum + Number(row.amountRemaining || 0), 0); }
  load(): void {
    if (!this.userId) { this.errorMessage = 'Sign in to load payment tracking.'; return; }
    this.loading = true; this.errorMessage = '';
    // Bills and payments are stored by the Bills service. The standalone tracking
    // service uses a separate table, so query the source records to keep the page
    // consistent with the Bills screen and its configured numeric account ID.
    this.api.getBills(environment.billUserId).subscribe({
      next: bills => this.loadBillPayments(bills || []),
      error: e => { this.errorMessage = apiErrorMessage(e, 'Could not load bills for tracking. Check the Bills service on port 8080.'); this.loading = false; }
    });
  }

  private loadBillPayments(bills: Bill[]): void {
    const identifiedBills = bills.filter((bill): bill is Bill & { billId: number } => typeof bill.billId === 'number' && Number.isFinite(bill.billId));
    if (!identifiedBills.length) {
      this.statuses = [];
      this.history = [];
      this.loading = false;
      return;
    }

    forkJoin(identifiedBills.map(bill => this.api.getBillPayments(bill.billId).pipe(catchError(() => of([] as BillPayment[])))))
      .subscribe(paymentGroups => {
        const today = new Date().toISOString().slice(0, 10);
        this.statuses = identifiedBills
          .filter(bill => bill.dueDate >= this.fromDate && bill.dueDate <= this.toDate)
          .map((bill, index) => {
            const successful = (paymentGroups[index] || []).filter(payment => (payment.paymentStatus || 'SUCCESS').toUpperCase() === 'SUCCESS');
            const paidFromTransactions = successful.reduce((sum, payment) => sum + Number(payment.paymentAmount || 0), 0);
            const totalAmount = Number(bill.amount || 0);
            const amountPaid = (bill.status || '').toUpperCase() === 'PAID' ? Math.max(paidFromTransactions, totalAmount) : Math.min(paidFromTransactions, totalAmount);
            const amountRemaining = Math.max(totalAmount - amountPaid, 0);
            const status = amountRemaining === 0 ? 'PAID' : amountPaid > 0 ? 'PARTIALLY_PAID' : bill.dueDate < today ? 'OVERDUE' : 'PENDING';
            return {
              paymentId: bill.billId,
              billName: bill.billName,
              billCategory: bill.billCategory,
              totalAmount,
              amountPaid,
              amountRemaining,
              feeAmount: 0,
              dueDate: bill.dueDate,
              daysOverdue: status === 'OVERDUE' ? Math.floor((Date.parse(today) - Date.parse(bill.dueDate)) / 86400000) : 0,
              status,
              progressPercent: totalAmount > 0 ? Math.min(100, Math.round((amountPaid / totalAmount) * 10000) / 100) : 100
            } as PaymentStatus;
          });

        this.history = identifiedBills.flatMap((bill, index) => (paymentGroups[index] || [])
          .filter(payment => (payment.paymentStatus || 'SUCCESS').toUpperCase() === 'SUCCESS' && !!payment.paymentDate && payment.paymentDate >= this.fromDate && payment.paymentDate <= this.toDate)
          .map(payment => ({
            paymentId: payment.paymentId || bill.billId,
            billName: bill.billName,
            billCategory: bill.billCategory,
            amountPaid: Number(payment.paymentAmount || 0),
            dueDate: bill.dueDate,
            paidAt: payment.paymentDate!,
            paymentMethod: payment.paymentMethod || 'Payment'
          } as PaymentHistory)));
        this.loading = false;
      });
  }
}
