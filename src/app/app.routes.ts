import { inject } from '@angular/core';
import { Routes, Router } from '@angular/router';
import { SessionService } from './core/session.service';
import { ShellComponent } from './pages/shell.component';
import { LoginComponent } from './pages/login.component';
import { RegisterComponent } from './pages/register.component';
import { ForgotPasswordComponent } from './pages/forgot-password.component';
import { DashboardComponent } from './pages/dashboard.component';
import { BillsComponent } from './pages/bills.component';
import { ScheduleComponent } from './pages/schedule.component';
import { TrackingComponent } from './pages/tracking.component';
import { ProfileComponent } from './pages/profile.component';

const signedIn = () => inject(SessionService).isLoggedIn || inject(Router).createUrlTree(['/login']);

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: '', component: ShellComponent, canActivate: [signedIn], children: [
    { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'bills', component: BillsComponent },
    { path: 'schedule', component: ScheduleComponent },
    { path: 'tracking', component: TrackingComponent },
    { path: 'profile', component: ProfileComponent }
  ] },
  { path: '**', redirectTo: 'dashboard' }
];
