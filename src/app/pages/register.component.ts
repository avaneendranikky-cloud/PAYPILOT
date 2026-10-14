import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { IconComponent } from '../shared/icon.component';
import { icons } from '../shared/icons';
import { PaypilotApiService } from '../core/paypilot-api.service';
import { apiErrorMessage } from '../core/error-message';
import { RegisterPayload } from '../shared/models';

@Component({
  selector: 'pp-register', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink, IconComponent],
  template: `
  <main class="auth-layout auth-layout-compact">
    <section class="auth-brand-panel"><a class="brand brand-light" routerLink="/login"><span class="brand-mark"><pp-icon [icon]="icons.bolt" /></span><span>paypilot<span class="brand-dot">.</span><small>YOUR MONEY, IN MOTION</small></span></a><div class="auth-story"><div class="eyebrow light"><span></span> A better money rhythm starts here</div><h1>Make room for<br><em>what matters.</em></h1><p>Build a little more calm into every payment day.</p><div class="auth-stat"><strong>All your bills,</strong><span>in one beautiful place.</span></div></div><div class="auth-footnote"><pp-icon [icon]="icons.shield" /> Private from the first step</div></section>
    <section class="auth-form-panel"><div class="auth-mobile-brand"><a class="brand" routerLink="/login"><span class="brand-mark"><pp-icon [icon]="icons.bolt" /></span><span>paypilot<span class="brand-dot">.</span></span></a></div>
      <div class="auth-form-wrap register-wrap" *ngIf="!awaitingOtp"><div class="eyebrow"><span></span> START WITH CLARITY</div><h2>Create your account</h2><p class="auth-subtitle">A few details and you’re ready to go.</p>
        <div class="alert alert-error" *ngIf="errorMessage">{{ errorMessage }}</div>
        <form [formGroup]="form" (ngSubmit)="register()" class="form-stack">
          <div class="field-grid"><label class="field"><span>PayPilot user ID</span><input formControlName="userId" placeholder="e.g. alex01" autocomplete="username"></label><label class="field"><span>Email address</span><input type="email" formControlName="email" placeholder="you@example.com" autocomplete="email"></label></div>
          <div class="field-grid"><label class="field"><span>PAN number</span><input formControlName="panNumber" placeholder="Use test data for sandbox"></label><label class="field"><span>Bank account number</span><input formControlName="bankAccountNumber" placeholder="Test account number"></label></div>
          <div class="field-grid"><label class="field"><span>IFSC code</span><input formControlName="ifscCode" placeholder="Test IFSC"></label><label class="field"><span>Banking partner</span><input formControlName="bankingPartner" placeholder="Bank name"></label></div>
          <div class="field-grid"><label class="field"><span>Password</span><input type="password" formControlName="password" placeholder="Create a password" autocomplete="new-password"></label><label class="field"><span>Confirm password</span><input type="password" formControlName="confirmPassword" placeholder="Repeat password" autocomplete="new-password"></label></div>
          <p class="form-note"><pp-icon [icon]="icons.shield" /> Use dummy financial details in a test database. A verification code will be sent to your email.</p>
          <button class="button button-primary button-wide" type="submit" [disabled]="loading">{{ loading ? 'Creating account…' : 'Create account' }} <pp-icon [icon]="icons.external" /></button>
        </form><p class="auth-switch">Already have an account? <a routerLink="/login">Sign in <span>→</span></a></p>
      </div>
      <div class="auth-form-wrap" *ngIf="awaitingOtp"><div class="eyebrow"><span></span> ONE LAST STEP</div><h2>Check your inbox</h2><p class="auth-subtitle">Enter the registration code sent to <b>{{ email }}</b>.</p><div class="alert alert-error" *ngIf="errorMessage">{{ errorMessage }}</div><div class="alert alert-success" *ngIf="successMessage">{{ successMessage }}</div>
        <form [formGroup]="otpForm" (ngSubmit)="verify()" class="form-stack"><label class="field"><span>Verification code</span><input formControlName="otp" inputmode="numeric" autocomplete="one-time-code" placeholder="Enter your OTP"></label><button class="button button-primary button-wide" type="submit" [disabled]="loading">{{ loading ? 'Verifying…' : 'Verify email' }} <pp-icon [icon]="icons.check" /></button></form>
      </div><div class="auth-bottom">© 2026 PayPilot <span>Made for clarity.</span></div>
    </section>
  </main>`
})
export class RegisterComponent {
  icons = icons; loading = false; awaitingOtp = false; email = ''; errorMessage = ''; successMessage = '';
  form = this.fb.nonNullable.group({ userId: ['', Validators.required], email: ['', [Validators.required, Validators.email]], panNumber: ['', Validators.required], bankAccountNumber: ['', Validators.required], ifscCode: ['', Validators.required], bankingPartner: ['', Validators.required], password: ['', Validators.required], confirmPassword: ['', Validators.required] });
  otpForm = this.fb.nonNullable.group({ otp: ['', Validators.required] });
  constructor(private fb: FormBuilder, private api: PaypilotApiService, private router: Router) {}
  register(): void {
    this.errorMessage = '';
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const payload = this.form.getRawValue() as RegisterPayload;
    if (payload.password !== payload.confirmPassword) { this.errorMessage = 'Your passwords do not match.'; return; }
    this.email = payload.email; this.loading = true;
    this.api.register(payload).subscribe({ next: () => { this.awaitingOtp = true; this.successMessage = 'Account created. Check your email for the OTP.'; }, error: (error: unknown) => { this.errorMessage = apiErrorMessage(error, 'Could not create the account.'); this.loading = false; }, complete: () => this.loading = false });
  }
  verify(): void {
    this.errorMessage = '';
    if (this.otpForm.invalid) { this.otpForm.markAllAsTouched(); return; }
    this.loading = true;
    this.api.verifyRegistration(this.email, this.otpForm.controls.otp.value).subscribe({ next: () => { this.successMessage = 'Email verified. You can now sign in.'; setTimeout(() => this.router.navigateByUrl('/login'), 900); }, error: (error: unknown) => { this.errorMessage = apiErrorMessage(error, 'OTP verification failed.'); this.loading = false; }, complete: () => this.loading = false });
  }
}
