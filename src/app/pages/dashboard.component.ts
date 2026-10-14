import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PaypilotApiService } from '../core/paypilot-api.service';
import { apiErrorMessage } from '../core/error-message';
import { Bill, ScheduledPayment } from '../shared/models';
import { IconComponent } from '../shared/icon.component';
import { icons } from '../shared/icons';
import { riseIn } from '../shared/motion';

@Component({ selector: 'pp-dashboard', standalone: true, imports: [CommonModule, CurrencyPipe, RouterLink, IconComponent], animations: [riseIn], template: `
  <div class="page-stack" [@riseIn]="true">
    <section class="welcome-row"><div><div class="eyebrow"><span></span> YOUR FINANCIAL MOMENT</div><h1>Good {{ hour }}, <em>{{ userId }}</em><span class="wave">✦</span></h1><p class="page-lede">A little more clarity for everything you’ve got planned.</p></div><a class="button button-dark" routerLink="/bills"><pp-icon [icon]="icons.plus" /> Add a bill</a></section>
    <div class="alert alert-error" *ngIf="errorMessage">{{ errorMessage }}</div>
    <section class="summary-grid">
      <article class="summary-card summary-card-dark"><div class="summary-top"><span>OPEN BILLS</span><span class="summary-icon"><pp-icon [icon]="icons.invoice" /></span></div><strong>{{ bills.length | number:'2.0' }}</strong><div class="summary-foot"><span class="status-dot mint-dot"></span>{{ bills.length ? 'Keep your next due date in view' : 'Your bill list is ready' }}</div><span class="card-shape shape-a"></span></article>
      <article class="summary-card"><div class="summary-top"><span>AMOUNT DUE</span><span class="summary-icon icon-mint"><pp-icon [icon]="icons.wallet" /></span></div><strong>{{ amountDue | currency:'INR':'symbol':'1.0-0' }}</strong><div class="summary-foot"><span class="status-dot coral-dot"></span>Across your current bills</div><span class="card-shape shape-b"></span></article>
      <article class="summary-card"><div class="summary-top"><span>SCHEDULED</span><span class="summary-icon icon-lavender"><pp-icon [icon]="icons.calendarCheck" /></span></div><strong>{{ activeSchedules | number:'2.0' }}</strong><div class="summary-foot"><span class="status-dot purple-dot"></span>Payments set to happen</div><span class="card-shape shape-c"></span></article>
    </section>
    <section class="dashboard-grid">
      <article class="panel upcoming-panel"><div class="panel-heading"><div><div class="eyebrow"><span></span> STAY AHEAD</div><h2>Coming up</h2></div><a routerLink="/bills" class="text-link">View all <span>↗</span></a></div>
        <div class="empty-state" *ngIf="!loading && bills.length === 0"><span class="empty-icon"><pp-icon [icon]="icons.calendar" /></span><strong>Nothing due just yet</strong><p>Add your first bill and we’ll keep it on your radar.</p><a routerLink="/bills" class="text-link">Add a bill <span>→</span></a></div>
        <div class="upcoming-list" *ngIf="bills.length"><div class="upcoming-row" *ngFor="let bill of bills | slice:0:4"><div class="bill-monogram">{{ bill.billName.charAt(0) }}</div><div class="bill-main"><strong>{{ bill.billName }}</strong><small>{{ bill.billCategory }} · Due {{ bill.dueDate | date:'MMM d' }}</small></div><div class="bill-amount">{{ bill.amount | currency:'INR':'symbol':'1.0-0' }}<small [class.text-coral]="bill.status !== 'PAID'">{{ bill.status || 'PENDING' }}</small></div></div></div>
      </article>
      <article class="panel rhythm-panel"><div class="panel-heading"><div><div class="eyebrow"><span></span> YOUR ROUTINE</div><h2>Payment rhythm</h2></div><span class="panel-orb"><pp-icon [icon]="icons.sparkles" /></span></div><div class="rhythm-art"><div class="rhythm-ring ring-large"></div><div class="rhythm-ring ring-small"></div><div class="rhythm-core"><pp-icon [icon]="icons.bolt" /></div><div class="rhythm-pill pill-a">{{ bills.length }} bills</div><div class="rhythm-pill pill-b">{{ activeSchedules }} scheduled</div></div><p class="rhythm-copy">A clear view today makes tomorrow feel lighter.</p><a routerLink="/tracking" class="button button-soft">Explore payment tracking <pp-icon [icon]="icons.external" /></a></article>
    </section>
    <section class="quick-links"><div><div class="eyebrow"><span></span> YOUR NEXT MOVE</div><h2>Good money habits, made simple.</h2></div><div class="quick-link-list"><a routerLink="/schedule"><span class="quick-icon mint-bg"><pp-icon [icon]="icons.calendar" /></span><span><b>Set a schedule</b><small>Stay ahead of repeat payments</small></span><span class="quick-arrow">↗</span></a><a routerLink="/tracking"><span class="quick-icon lavender-bg"><pp-icon [icon]="icons.chart" /></span><span><b>See your progress</b><small>Understand every payment</small></span><span class="quick-arrow">↗</span></a></div></section>
  </div>` })
export class DashboardComponent implements OnInit {
  icons = icons; bills: Bill[] = []; schedules: ScheduledPayment[] = []; loading = true; errorMessage = ''; userId = 'there';
  constructor(private api: PaypilotApiService) {}
  ngOnInit(): void {
    this.userId = sessionStorage.getItem('paypilot.session') ? (JSON.parse(sessionStorage.getItem('paypilot.session') || '{}').userId || 'there') : 'there';
    this.api.getBills().subscribe({ next: rows => this.bills = rows || [], error: e => { this.errorMessage = apiErrorMessage(e, 'Bills could not load. Check the Bills backend and its database connection.'); this.loading = false; }, complete: () => this.loading = false });
    this.api.getSchedules().subscribe({ next: rows => this.schedules = rows || [], error: () => undefined });
  }
  get amountDue(): number { return this.bills.filter(bill => bill.status !== 'PAID').reduce((sum, bill) => sum + Number(bill.amount || 0), 0); }
  get activeSchedules(): number { return this.schedules.filter(item => item.active).length; }
  get hour(): string { const h = new Date().getHours(); return h < 12 ? 'morning' : h < 17 ? 'afternoon' : 'evening'; }
}
