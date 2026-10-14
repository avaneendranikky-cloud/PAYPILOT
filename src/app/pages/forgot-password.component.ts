import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../shared/icon.component';
import { icons } from '../shared/icons';
import { PaypilotApiService } from '../core/paypilot-api.service';
import { apiErrorMessage } from '../core/error-message';

@Component({ selector: 'pp-forgot-password', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink, IconComponent], template: `
  <main class="auth-layout"><section class="auth-brand-panel"><a class="brand brand-light" routerLink="/login"><span class="brand-mark"><pp-icon [icon]="icons.bolt" /></span><span>paypilot<span class="brand-dot">.</span><small>YOUR MONEY, IN MOTION</small></span></a><div class="auth-story"><div class="eyebrow light"><span></span> ACCOUNT RECOVERY</div><h1>Back to your<br><em>better rhythm.</em></h1><p>We’ll help you get back in securely.</p></div><div class="auth-footnote"><pp-icon [icon]="icons.shield" /> Your account stays yours</div></section>
  <section class="auth-form-panel"><div class="auth-mobile-brand"><a class="brand" routerLink="/login"><span class="brand-mark"><pp-icon [icon]="icons.bolt" /></span><span>paypilot<span class="brand-dot">.</span></span></a></div><div class="auth-form-wrap"><div class="eyebrow"><span></span> RESET PASSWORD</div><h2>{{ step === 1 ? 'Find your account' : step === 2 ? 'Verify your email' : 'Create a new password' }}</h2><p class="auth-subtitle">{{ step === 1 ? 'We’ll send a one-time code to your registered email.' : step === 2 ? 'Enter the code from your email.' : 'Choose a new password for your account.' }}</p>
    <div class="alert alert-error" *ngIf="errorMessage">{{ errorMessage }}</div><div class="alert alert-success" *ngIf="successMessage">{{ successMessage }}</div>
    <form [formGroup]="form" (ngSubmit)="submit()" class="form-stack"><label class="field" *ngIf="step === 1 || step === 2"><span>Email address</span><input type="email" formControlName="email" placeholder="you@example.com"></label><label class="field" *ngIf="step === 2"><span>One-time code</span><input formControlName="otp" inputmode="numeric" placeholder="Enter OTP"></label><ng-container *ngIf="step === 3"><label class="field"><span>New password</span><input type="password" formControlName="newPassword" autocomplete="new-password"></label><label class="field"><span>Confirm new password</span><input type="password" formControlName="confirmPassword" autocomplete="new-password"></label></ng-container><button class="button button-primary button-wide" [disabled]="loading">{{ loading ? 'Please wait…' : step === 1 ? 'Send code' : step === 2 ? 'Verify code' : 'Reset password' }} <pp-icon [icon]="icons.arrow" /></button></form><p class="auth-switch"><a routerLink="/login">← Back to sign in</a></p></div><div class="auth-bottom">© 2026 PayPilot <span>Made for clarity.</span></div></section></main>` })
export class ForgotPasswordComponent {
  icons = { ...icons, arrow: icons.external }; step = 1; loading = false; errorMessage = ''; successMessage = '';
  form = this.fb.nonNullable.group({ email: ['', [Validators.required, Validators.email]], otp: [''], newPassword: [''], confirmPassword: [''] });
  constructor(private fb: FormBuilder, private api: PaypilotApiService) {}
  submit(): void {
    this.errorMessage = ''; this.successMessage = '';
    const v = this.form.getRawValue();
    if (this.step === 1 && !v.email) { this.form.controls.email.markAsTouched(); this.errorMessage = 'Enter your registered email.'; return; }
    if (this.step === 2 && !v.otp) { this.errorMessage = 'Enter the OTP from your email.'; return; }
    if (this.step === 3 && (!v.newPassword || v.newPassword !== v.confirmPassword)) { this.errorMessage = 'Enter matching new passwords.'; return; }
    this.loading = true;
    const call = this.step === 1 ? this.api.forgotPassword(v.email) : this.step === 2 ? this.api.verifyResetOtp(v.email, v.otp) : this.api.resetPassword(v.email, v.newPassword, v.confirmPassword);
    call.subscribe({ next: result => { if (this.step < 3) { this.step++; this.successMessage = this.step === 2 ? 'Code sent. Check your inbox.' : 'Code verified. Set your new password.'; } else { this.successMessage = result.message || 'Password reset successfully. Return to sign in.'; } }, error: (error: unknown) => { this.errorMessage = apiErrorMessage(error, 'Could not complete password reset.'); this.loading = false; }, complete: () => this.loading = false });
  }
}
