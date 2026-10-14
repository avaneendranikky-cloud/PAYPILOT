# PayPilot Angular 16 frontend

A responsive, premium-style Angular 16 interface for PayPilot. It includes account registration with email OTP verification, sign-in, password recovery, dashboard, bill and payment management, reminders, scheduled payments, payment tracking, and account settings.

## Requirements

- Node.js `^16.14.0` or `^18.10.0` and npm. Angular 16.2 supports TypeScript `<5.2` and RxJS 6.5/7; this project pins compatible versions in `package.json`.
- The PayPilot Spring Boot services and Oracle database configured and running. The API proxy targets are listed in `proxy.conf.json`.

## Install and run

1. Extract the ZIP.
2. Open a terminal in the extracted `paypilot-angular` folder.
3. Install project dependencies:

   ```bash
   npm install
   ```

4. Start the Angular dev server:

   ```bash
   npm start
   ```

5. Open `http://localhost:4200` in a browser. Register with test data in a test database, verify the emailed OTP, and sign in.

To create a production build, run `npm run build`; output is written to `dist/paypilot`.

## Styling, animations, and icons

- Styling is custom SCSS in `src/styles.scss`; no Bootstrap or Angular Material install is needed.
- `@angular/animations` is included. `provideAnimations()` is enabled in `src/main.ts`; route transitions and UI motion are in `src/app/shared/motion.ts` and SCSS.
- Font Awesome Angular and the free solid icon pack are included in `package.json`. The reusable `<pp-icon>` component is in `src/app/shared/icon.component.ts`.
- All required dependencies install with `npm install`. If adding these packages to a fresh Angular 16 app manually, use:

  ```bash
  npm install @angular/animations@~16.2.12 @fortawesome/angular-fontawesome@~0.13.0 @fortawesome/fontawesome-svg-core@^6.7.2 @fortawesome/free-solid-svg-icons@^6.7.2
  ```

## Connect to the backend

The API proxy is configured for the current PayPilot services:

| Feature | Local Spring Boot port | API prefix |
|---|---:|---|
| Sign in, registration, OTP, password recovery | 8082 | `/api/users` |
| Bills, payments, reminders | 8080 | `/api/bills`, `/api/payments`, `/api/reminders` |
| Scheduled payments | 8083 | `/api/scheduled-payments` |
| Payment tracking | 8084 | `/api/v1` |

The tracking service is configured to use port `8084` in the local `C:\Users\azureuser\PAYPILOT\tracking\src\main\resources\application.properties`, matching `proxy.conf.json`. Bills uses port `8080`.

The Bills API expects a numeric `userId`, while authentication and tracking use a text PayPilot user ID. Set `billUserId` in `src/environments/environment.ts` to the numeric user ID present in the Bills database. This is a limitation in the current backend services; the frontend keeps the IDs configurable rather than assuming they are the same.

The frontend alone cannot save bills or schedules. Start Oracle and all four Spring Boot services before testing data actions. In Eclipse, import `C:\Users\azureuser\PAYPILOT` as existing Maven projects, then run these main classes using **Run As → Java Application** (or **Spring Boot App** if available):

| Project | Main class | Port |
|---|---|---:|
| `userauth` | `com.paypilot.PayPilotApplication` | 8082 |
| `Bill-Managing` | `com.example.paypilotapp.PaypilotApplication` | 8080 |
| `schedule` | `com.paypilot.PayPilotApplication` | 8083 |
| `tracking` | `com.paypilot.payments.PaymentsTrackingApplication` | 8084 |

Wait for each Eclipse Console to say the application started. Check that Oracle is running and that the database service names and credentials in each service's `application.properties` match your local Oracle setup. If a service fails during startup, its Eclipse Console will show the database/configuration error. The frontend reports a clear connection message and lets you retry; a failed request does not save data.

## Main source map

- `src/app/pages/login.component.ts` — sign-in
- `src/app/pages/register.component.ts` — registration and OTP verification
- `src/app/pages/forgot-password.component.ts` — password reset flow
- `src/app/pages/dashboard.component.ts` — account overview
- `src/app/pages/bills.component.ts` — bills, payment, and reminder actions
- `src/app/pages/schedule.component.ts` — scheduled payments
- `src/app/pages/tracking.component.ts` — progress and completed-payment history
- `src/app/pages/profile.component.ts` — session profile and sign-out
- `src/app/core/paypilot-api.service.ts` — backend API calls
- `src/environments/environment.ts` and `proxy.conf.json` — local backend settings

Passwords are sent to the existing authentication API and are not stored in browser storage. Registration and payment actions should be tried only against a test database with test data.
