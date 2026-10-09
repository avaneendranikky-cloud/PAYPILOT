# PayPilot User Case 4 - Payment Tracking Backend

Spring Boot REST backend for payment progress, overview, and history. Java 17, Spring Boot 3.5.16, Spring Web, Spring Data JPA, Validation, Oracle JDBC `ojdbc11`, Oracle 21c.

## Spring Initializr
Choose Maven, Java, Spring Boot 3.5.16, Java 17, Jar. Group `com.paypilot`, artifact `payments-tracking`, package `com.paypilot.payments`. Select Spring Web, Spring Data JPA, and Validation. This POM includes Oracle `ojdbc11` and Spring Boot Test.

## Oracle setup
Default JDBC service is `jdbc:oracle:thin:@//localhost:1521/XEPDB1`, matching the listener output provided. Set credentials in CMD and run:

```cmd
set DB_USERNAME=SYSTEM
set DB_PASSWORD=your_database_password
mvn spring-boot:run
```

For the requested local SYSTEM setup, connect as SYSTEM and run `src/main/resources/db/oracle-schema.sql` while logged into XEPDB1. The objects will be created in the SYSTEM schema. Hibernate validates but does not create/alter tables. This grants the application administrator-level database access and is not suitable for production; use a dedicated least-privilege schema there. Override DB_URL for another host, port, or service.

## Endpoints
All require `userId`, `fromDate`, and `toDate` (inclusive ISO `YYYY-MM-DD`). Category is optional.

- `GET /api/v1/users/{userId}/payments/progress?fromDate=2026-10-01&toDate=2026-10-31`
- `GET /api/v1/users/{userId}/payments/overview?fromDate=2026-10-01&toDate=2026-10-31&category=Utilities`
- `GET /api/v1/users/{userId}/payments/history?fromDate=2026-10-01&toDate=2026-10-31`

Progress and overview return amounts, fee, due date, overdue days, status, and percentage paid. History returns completed payments with date and method. Empty results return HTTP 200 and an empty array.

## Assumptions
Statuses: PAID when fully paid; PARTIALLY_PAID when some is paid; OVERDUE when unpaid and past due; DUE_SOON when due within the next 7 days; otherwise PENDING. History requires fully paid plus paidAt. Filters use due date in all views because the PDF does not specify another history date field. PDF's one step saying Payment Overview in the history flow is interpreted as Payment History. Amounts are per-bill; no aggregate total was specified. No authentication mechanism was specified: replace caller-supplied userId with the authenticated principal before production.

