import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { IconComponent } from '../shared/icon.component';
import { icons } from '../shared/icons';
import { SessionService } from '../core/session.service';

@Component({
  selector: 'pp-shell', standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, IconComponent],
  template: `
    <div class="app-frame">
      <aside class="sidebar">
        <a class="brand" routerLink="/dashboard"><span class="brand-mark"><pp-icon [icon]="icons.bolt" /></span><span>paypilot<span class="brand-dot">.</span><small>YOUR MONEY, IN MOTION</small></span></a>
        <div class="nav-label">WORKSPACE</div>
        <nav class="side-nav" aria-label="Main navigation">
          <a routerLink="/dashboard" routerLinkActive="active"><pp-icon [icon]="icons.dashboard" /> Overview</a>
          <a routerLink="/bills" routerLinkActive="active"><pp-icon [icon]="icons.invoice" /> Bills <span class="nav-count">01</span></a>
          <a routerLink="/schedule" routerLinkActive="active"><pp-icon [icon]="icons.calendar" /> Schedules</a>
          <a routerLink="/tracking" routerLinkActive="active"><pp-icon [icon]="icons.chart" /> Payment tracking</a>
        </nav>
        <div class="sidebar-bottom">
          <div class="help-card"><span class="help-orb"><pp-icon [icon]="icons.shield" /></span><strong>Peace of mind,<br>built in.</strong><p>Your payment details stay in your control.</p></div>
          <a class="profile-link" routerLink="/profile"><span class="avatar">{{ initials }}</span><span class="profile-copy"><b>{{ displayName }}</b><small>Personal account</small></span><pp-icon [icon]="icons.more" /></a>
        </div>
      </aside>
      <main class="main-column">
        <header class="topbar">
          <div class="breadcrumb"><span>PayPilot</span><span class="slash">/</span><b>{{ pageTitle }}</b></div>
          <div class="top-actions"><span class="today-label">{{ today | date:'EEEE, MMM d' }}</span><button class="icon-button" aria-label="Notifications"><pp-icon [icon]="icons.bell" /><i></i></button><button class="top-avatar" routerLink="/profile">{{ initials }}</button></div>
        </header>
        <section class="page-content">
          <router-outlet #outlet="outlet"></router-outlet>
        </section>
        <footer class="app-footer">
          <a class="footer-brand" routerLink="/dashboard"><span class="footer-mark"><pp-icon [icon]="icons.bolt" /></span>paypilot<span class="brand-dot">.</span></a>
          <span class="footer-note">A little more clarity for your money, every day.</span>
          <span class="footer-meta">© {{ today | date:'yyyy' }} PayPilot <i></i> Built for peace of mind</span>
        </footer>
      </main>
    </div>`
})
export class ShellComponent {
  icons = icons;
  today = new Date();
  constructor(public session: SessionService, private router: Router) {}
  get displayName(): string { return this.session.user?.userId || 'Your account'; }
  get initials(): string { return this.displayName.slice(0, 2).toUpperCase(); }
  get pageTitle(): string {
    const title = this.router.url.split('/')[1] || 'dashboard';
    return title.charAt(0).toUpperCase() + title.slice(1).replace('-', ' ');
  }
}
