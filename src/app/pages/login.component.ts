import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { IconComponent } from '../shared/icon.component';
import { icons } from '../shared/icons';
import { PaypilotApiService } from '../core/paypilot-api.service';
import { SessionService } from '../core/session.service';
import { apiErrorMessage } from '../core/error-message';
import { routeFade } from '../shared/motion';

@Component({
  selector: 'pp-login', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink, IconComponent], animations: [routeFade],
  template: `
  <main class="auth-layout" [@routeFade]="true">
    <section class="auth-brand-panel"><a class="brand brand-light" routerLink="/login"><span class="brand-mark"><pp-icon [icon]="icons.bolt" /></span><span>paypilot<span class="brand-dot">.</span><small>YOUR MONEY, IN MOTION</small></span></a>
      <div class="auth-story"><div class="eyebrow light"><span></span> A calmer way to manage money</div><h1>Every bill.<br><em>Beautifully</em><br>under control.</h1><p>One clear view of what’s due, what’s paid, and what comes next.</p><div class="story-visual"><div class="visual-orbit orbit-one"></div><div class="visual-orbit orbit-two"></div><div class="visual-center"><pp-icon [icon]="icons.wallet" /></div><div class="visual-chip chip-one"><span class="chip-dot mint"></span> Bills, handled</div><div class="visual-chip chip-two"><span class="chip-dot coral"></span> On your time</div></div></div>
      <div class="auth-footnote"><pp-icon [icon]="icons.shield" /> Secure by design <span>·</span> Clear by default</div>
    </section>
    <section class="auth-form-panel"><div class="auth-mobile-brand"><a class="brand" routerLink="/login"><span class="brand-mark"><pp-icon [icon]="icons.bolt" /></span><span>paypilot<span class="brand-dot">.</span></span></a></div>
      <div class="auth-form-wrap"><div class="eyebrow"><span></span> WELCOME BACK</div><h2>Sign in to PayPilot</h2><p class="auth-subtitle">Pick up right where you left off.</p>
        <div class="alert alert-error" *ngIf="errorMessage">{{ errorMessage }}</div>
        <form [formGroup]="form" (ngSubmit)="submit()" class="form-stack">
          <label class="field"><span>User ID</span><input formControlName="userId" placeholder="Your PayPilot ID" autocomplete="username"><small *ngIf="form.controls.userId.touched && form.controls.userId.invalid">Enter your user ID.</small></label>
          <label class="field"><span>Password <a routerLink="/forgot-password">Forgot password?</a></span><div class="password-input"><input [type]="showPassword ? 'text' : 'password'" formControlName="password" placeholder="Enter your password" autocomplete="current-password"><button type="button" (click)="showPassword = !showPassword" [attr.aria-label]="showPassword ? 'Hide password' : 'Show password'"><pp-icon [icon]="showPassword ? icons.eyeOff : icons.eye" /></button></div><small *ngIf="form.controls.password.touched && form.controls.password.invalid">Enter your password.</small></label>
          <button class="button button-primary button-wide" type="submit" [disabled]="loading">{{ loading ? 'Signing in…' : 'Sign in' }} <pp-icon [icon]="icons.external" /></button>
        </form>
        <p class="auth-switch">New to PayPilot? <a routerLink="/register">Create an account <span>→</span></a></p>
        <div class="auth-safe"><pp-icon [icon]="icons.shield" /> Your account details are protected.</div>
      </div><div class="auth-bottom">© 2026 PayPilot <span>Made for clarity.</span></div>
    </section>
  </main>`
})
export class LoginComponent {
  icons = icons; loading = false; showPassword = false; errorMessage = '';
  form = this.fb.nonNullable.group({ userId: ['', Validators.required], password: ['', Validators.required] });
  constructor(private fb: FormBuilder, private api: PaypilotApiService, private session: SessionService, private router: Router) {}
  submit(): void {
    this.errorMessage = '';
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.api.login(this.form.getRawValue()).subscribe({
      next: () => { this.session.signIn(this.form.controls.userId.value); this.router.navigateByUrl('/dashboard'); },
      error: (error: unknown) => { this.errorMessage = apiErrorMessage(error, 'Could not sign in. Check your account and try again.'); this.loading = false; },
      complete: () => this.loading = false
    });
  }
}
