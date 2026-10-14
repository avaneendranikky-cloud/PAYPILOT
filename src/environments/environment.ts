export const environment = {
  production: false,
  // The Angular dev-server proxy routes each API to its Spring Boot service.
  authApi: '',
  billsApi: '',
  scheduleApi: '',
  trackingApi: '',
  // Bill endpoints require a numeric database userId; configure it for your test account.
  billUserId: 1
};
