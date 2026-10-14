import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { SessionService } from '../core/session.service';
import { IconComponent } from '../shared/icon.component';
import { icons } from '../shared/icons';

@Component({ selector: 'pp-profile', standalone: true, imports: [CommonModule, RouterLink, IconComponent], template: `
  <div class="page-stack"><section class="page-title-row"><div><div class="eyebrow"><span></span> YOUR SPACE</div><h1>Account <em>settings</em></h1><p class="page-lede">A simple look at your PayPilot profile.</p></div></section><section class="panel profile-panel"><span class="profile-large">{{ initials }}</span><div class="profile-details"><div class="eyebrow"><span></span> PERSONAL ACCOUNT</div><h2>{{ userId }}</h2><p>Your PayPilot sign-in ID</p><div class="profile-status"><span class="status-dot mint-dot"></span> Signed in for this browser session</div></div></section><section class="panel security-panel"><span class="quick-icon mint-bg"><pp-icon [icon]="icons.shield" /></span><div><h3>Keep your account secure</h3><p>Sign out when you’re using a shared computer. PayPilot doesn’t store your password in this browser.</p></div><button class="button button-ghost" (click)="logout()"><pp-icon [icon]="icons.logout" /> Sign out</button></section><p class="config-note">Bill screens use the numeric account ID configured for your backend. Update <code>billUserId</code> in <code>src/environments/environment.ts</code> if your Bills service uses a different ID.</p></div>` })
export class ProfileComponent {
  icons = icons;
  constructor(public session: SessionService, private router: Router) {}
  get userId(): string { return this.session.user?.userId || 'PayPilot user'; }
  get initials(): string { return this.userId.slice(0, 2).toUpperCase(); }
  logout(): void { this.session.signOut(); this.router.navigateByUrl('/login'); }
}
