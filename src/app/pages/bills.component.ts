import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PaypilotApiService } from '../core/paypilot-api.service';
import { Bill } from '../shared/models';
import { environment } from '../../environments/environment';
import { apiErrorMessage } from '../core/error-message';
import { IconComponent } from '../shared/icon.component';
import { icons } from '../shared/icons';
import { riseIn } from '../shared/motion';

@Component({ selector: 'pp-bills', standalone: true, imports: [CommonModule, CurrencyPipe, DatePipe, ReactiveFormsModule, IconComponent], animations: [riseIn], template: `
  <div class="page-stack" [@riseIn]="true">
    <section class="page-title-row"><div><div class="eyebrow"><span></span> YOUR MONEY, ORGANIZED</div><h1>Bills <em>& payments</em></h1><p class="page-lede">The little things, handled before they become big things.</p></div><button class="button button-primary" (click)="openForm()"><pp-icon [icon]="icons.plus" /> Add a bill</button></section>
    <div class="alert alert-error" *ngIf="errorMessage">{{ errorMessage }}</div><div class="alert alert-success" *ngIf="successMessage">{{ successMessage }}</div>
    <section class="bill-toolbar"><div class="filter-tabs"><button [class.selected]="filter === 'ALL'" (click)="filter='ALL'">All bills <span>{{ bills.length }}</span></button><button [class.selected]="filter === 'PENDING'" (click)="filter='PENDING'">Upcoming</button><button [class.selected]="filter === 'PAID'" (click)="filter='PAID'">Paid</button></div><div class="toolbar-note"><span class="status-dot mint-dot"></span> Synced with your account</div></section>
    <section class="panel table-panel"><div class="table-scroll"><table class="data-table"><thead><tr><th>BILL</th><th>CATEGORY</th><th>DUE DATE</th><th>AMOUNT</th><th>STATUS</th><th></th></tr></thead><tbody>
      <tr *ngFor="let bill of visibleBills"><td><div class="table-bill"><span class="bill-monogram">{{ bill.billName.charAt(0) }}</span><span><b>{{ bill.billName }}</b><small>#{{ bill.billId }}</small></span></div></td><td>{{ bill.billCategory }}</td><td>{{ bill.dueDate | date:'MMM d, y' }}</td><td class="amount-cell">{{ bill.amount | currency:'INR':'symbol':'1.0-2' }}</td><td><span class="status-badge" [ngClass]="(bill.status || 'PENDING').toLowerCase()">{{ bill.status || 'PENDING' }}</span></td><td><div class="row-actions"><button *ngIf="bill.status !== 'PAID'" class="mini-action" title="Pay bill" (click)="pay(bill)"><pp-icon [icon]="icons.check" /></button><button *ngIf="bill.billId" class="mini-action" title="Create reminder" (click)="remind(bill)"><pp-icon [icon]="icons.bell" /></button><button class="mini-action danger" title="Delete bill" (click)="remove(bill)"><pp-icon [icon]="icons.close" /></button></div></td></tr>
      <tr *ngIf="!loading && visibleBills.length === 0"><td colspan="6"><div class="empty-state compact"><span class="empty-icon"><pp-icon [icon]="icons.invoice" /></span><strong>{{ bills.length ? 'No bills in this view' : 'Your first bill starts here' }}</strong><p>{{ bills.length ? 'Try a different filter.' : 'Add a bill to get a clear view of your upcoming payments.' }}</p></div></td></tr>
    </tbody></table></div><div class="table-footer"><span>Showing {{ visibleBills.length }} of {{ bills.length }} bills</span><span>Currency · INR</span></div></section>
    <div class="modal-backdrop" *ngIf="formOpen" (click)="closeForm()"><section class="modal-card" (click)="$event.stopPropagation()"><div class="modal-head"><div><div class="eyebrow"><span></span> NEW PAYMENT</div><h2>Add a bill</h2></div><button class="icon-button" (click)="closeForm()" aria-label="Close"><pp-icon [icon]="icons.close" /></button></div><form [formGroup]="form" (ngSubmit)="save()" class="form-stack"><div class="field-grid"><label class="field"><span>Bill name</span><input formControlName="billName" placeholder="Electricity"></label><label class="field"><span>Category</span><select formControlName="billCategory"><option value="Utilities">Utilities</option><option value="Housing">Housing</option><option value="Internet">Internet</option><option value="Insurance">Insurance</option><option value="Education">Education</option><option value="Other">Other</option></select></label></div><div class="field-grid"><label class="field"><span>Amount (₹)</span><input type="number" min="0.01" formControlName="amount" placeholder="0.00"></label><label class="field"><span>Due date</span><input type="date" formControlName="dueDate"></label></div><div class="field-grid"><label class="field"><span>Bill date</span><input type="date" formControlName="billDate"></label><label class="field"><span>Repeats</span><select formControlName="recurringBill"><option value="N">One time</option><option value="Y">Recurring</option></select></label></div><label class="field"><span>Notes <small>(optional)</small></span><textarea rows="2" formControlName="notes" placeholder="Add a note for future you"></textarea></label><div class="modal-actions"><button type="button" class="button button-ghost" (click)="closeForm()">Cancel</button><button class="button button-primary" [disabled]="saving">{{ saving ? 'Saving…' : 'Save bill' }}</button></div></form></section></div>
  </div>` })
export class BillsComponent implements OnInit {
  icons = icons; bills: Bill[] = []; filter = 'ALL'; loading = true; saving = false; formOpen = false; errorMessage = ''; successMessage = '';
  form = this.fb.nonNullable.group({ billName: ['', Validators.required], billCategory: ['Utilities', Validators.required], billDate: [this.today, Validators.required], dueDate: [this.today, Validators.required], amount: [0, [Validators.required, Validators.min(0.01)]], reminderFrequency: ['ONCE'], notes: [''], recurringBill: ['N'], status: ['PENDING'] });
  constructor(private api: PaypilotApiService, private fb: FormBuilder) {}
  ngOnInit(): void { this.load(); }
  get today(): string { return new Date().toISOString().slice(0, 10); }
  get visibleBills(): Bill[] { return this.filter === 'ALL' ? this.bills : this.bills.filter(item => (item.status || 'PENDING').toUpperCase() === this.filter); }
  load(): void { this.loading = true; this.api.getBills().subscribe({ next: rows => this.bills = rows || [], error: e => { this.errorMessage = apiErrorMessage(e, 'Could not load bills. Check the Bills API at port 8080.'); this.loading = false; }, complete: () => this.loading = false }); }
  openForm(): void { this.errorMessage = ''; this.formOpen = true; }
  closeForm(): void { this.formOpen = false; }
  save(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving = true; this.errorMessage = '';
    const body = { ...this.form.getRawValue(), userId: environment.billUserId } as Bill;
    this.api.createBill(body).subscribe({ next: created => { this.bills = [created, ...this.bills]; this.formOpen = false; this.successMessage = 'Your bill was added.'; this.form.reset({ billName: '', billCategory: 'Utilities', billDate: this.today, dueDate: this.today, amount: 0, reminderFrequency: 'ONCE', notes: '', recurringBill: 'N', status: 'PENDING' }); }, error: e => { this.errorMessage = apiErrorMessage(e, 'Could not save the bill. Your entries are still in the form; retry when the Bills service is available.'); this.saving = false; }, complete: () => this.saving = false });
  }
  pay(bill: Bill): void {
    if (!bill.billId) return;
    this.errorMessage = '';
    this.api.payBill(bill.billId, { paymentAmount: bill.amount, paymentMethod: 'UPI' }).subscribe({ next: () => { bill.status = 'PAID'; this.successMessage = `${bill.billName} marked paid.`; }, error: e => this.errorMessage = apiErrorMessage(e, 'Payment could not be recorded.') });
  }
  remind(bill: Bill): void {
    if (!bill.billId) return;
    this.errorMessage = '';
    this.api.createReminder({ billId: bill.billId, reminderFrequency: bill.reminderFrequency || 'WEEKLY', startDate: bill.dueDate, message: `Payment reminder for ${bill.billName}`, notificationType: 'EMAIL', recurringBill: bill.recurringBill || 'N' }).subscribe({ next: () => this.successMessage = `Reminder created for ${bill.billName}.`, error: e => this.errorMessage = apiErrorMessage(e, 'Could not create reminder.') });
  }
  remove(bill: Bill): void {
    if (!bill.billId || !window.confirm(`Delete ${bill.billName}?`)) return;
    this.api.deleteBill(bill.billId).subscribe({ next: () => { this.bills = this.bills.filter(row => row.billId !== bill.billId); this.successMessage = 'Bill deleted.'; }, error: e => this.errorMessage = apiErrorMessage(e, 'Could not delete bill.') });
  }
}
