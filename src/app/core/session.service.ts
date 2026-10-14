import { Injectable } from '@angular/core';
import { SessionUser } from '../shared/models';

const SESSION_KEY = 'paypilot.session';

@Injectable({ providedIn: 'root' })
export class SessionService {
  get user(): SessionUser | null {
    try { return JSON.parse(sessionStorage.getItem(SESSION_KEY) ?? 'null') as SessionUser | null; }
    catch { return null; }
  }
  get isLoggedIn(): boolean { return !!this.user?.userId; }
  signIn(userId: string, email?: string): void {
    sessionStorage.setItem(SESSION_KEY, JSON.stringify({ userId, email } satisfies SessionUser));
  }
  signOut(): void { sessionStorage.removeItem(SESSION_KEY); }
}
