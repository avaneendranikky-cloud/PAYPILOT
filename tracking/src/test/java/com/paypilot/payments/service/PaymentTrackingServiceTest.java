package com.paypilot.payments.service;

import com.paypilot.payments.dto.PaymentStatusView;
import com.paypilot.payments.model.PaymentRecord;
import com.paypilot.payments.model.PaymentStatus;
import com.paypilot.payments.repository.PaymentRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentTrackingServiceTest {

    private static final int DUE_SOON_DAYS = 7;
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 6);
    private static final LocalDate FROM_DATE = TODAY.withDayOfMonth(1);
    private static final LocalDate TO_DATE = TODAY.withDayOfMonth(TODAY.lengthOfMonth());

    @Mock
    private PaymentRecordRepository repository;

    private PaymentTrackingService service;

    @BeforeEach
    void setUp() {
        // A fixed clock makes date calculations repeatable, independent of the computer's date.
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-06T12:00:00Z"), ZoneOffset.UTC);
        service = new PaymentTrackingService(repository, DUE_SOON_DAYS, fixedClock);
    }

    @Test
    void progressCalculatesStatusesAmountsAndOverdueDays() {
        List<PaymentRecord> records = List.of(
                payment("100.00", "0.00", TODAY.plusDays(30)),       // PENDING
                payment("100.00", "25.00", TODAY.plusDays(30)),      // PARTIALLY_PAID
                payment("100.00", "100.00", TODAY.plusDays(30)),     // PAID
                payment("80.00", "0.00", TODAY.plusDays(DUE_SOON_DAYS)), // DUE_SOON
                payment("80.00", "0.00", TODAY.minusDays(5)));       // OVERDUE
        when(repository.findForTracking("user-1", null, FROM_DATE, TO_DATE)).thenReturn(records);

        var result = service.progress("user-1", null, FROM_DATE, TO_DATE);

        assertEquals(records.size(), result.resultCount());
        assertEquals(List.of(PaymentStatus.PENDING, PaymentStatus.PARTIALLY_PAID, PaymentStatus.PAID,
                        PaymentStatus.DUE_SOON, PaymentStatus.OVERDUE),
                result.payments().stream().map(PaymentStatusView::status).toList());
        assertEquals(new BigDecimal("25.00"), result.payments().get(1).progressPercent());
        assertEquals(5L, result.payments().get(4).daysOverdue());
        assertEquals(new BigDecimal("80.00"), result.payments().get(4).amountRemaining());
    }

    @Test
    void overviewRequestsRecordsUsingUserCategoryAndDateRange() {
        when(repository.findForTracking("user-42", "Utilities", FROM_DATE, TO_DATE))
                .thenReturn(List.of(payment("50.00", "0.00", TODAY.plusDays(10))));

        var result = service.overview("user-42", "Utilities", FROM_DATE, TO_DATE);

        assertEquals(1, result.resultCount());
        verify(repository).findForTracking("user-42", "Utilities", FROM_DATE, TO_DATE);
    }

    @Test
    void historyContainsOnlyFullyPaidRecordsWithPaymentTimestamp() {
        PaymentRecord completed = payment("100.00", "100.00", TODAY.minusDays(4));
        completed.setPaidAt(TODAY.minusDays(4).atTime(9, 30));
        completed.setPaymentMethod("ACH");

        PaymentRecord paidWithoutTimestamp = payment("60.00", "60.00", TODAY.minusDays(3));
        PaymentRecord incomplete = payment("100.00", "50.00", TODAY.minusDays(2));
        incomplete.setPaidAt(TODAY.minusDays(2).atTime(10, 0));

        when(repository.findForTracking("user-1", "Utilities", FROM_DATE, TO_DATE))
                .thenReturn(List.of(completed, paidWithoutTimestamp, incomplete));

        var result = service.history("user-1", "Utilities", FROM_DATE, TO_DATE);

        assertEquals(1, result.resultCount());
        assertEquals("ACH", result.payments().get(0).paymentMethod());
        assertEquals(new BigDecimal("100.00"), result.payments().get(0).amountPaid());
    }

    @Test
    void zeroTotalHasNoRemainingBalanceAndReportsFullProgress() {
        when(repository.findForTracking("user-1", null, FROM_DATE, TO_DATE))
                .thenReturn(List.of(payment("0.00", "0.00", TODAY.plusDays(30))));

        var result = service.progress("user-1", null, FROM_DATE, TO_DATE);

        assertEquals(PaymentStatus.PAID, result.payments().get(0).status());
        assertEquals(new BigDecimal("100.00"), result.payments().get(0).progressPercent());
        assertEquals(new BigDecimal("0.00"), result.payments().get(0).amountRemaining());
    }

    @Test
    void emptyRepositoryResultProducesAnEmptyResponse() {
        when(repository.findForTracking("user-1", null, FROM_DATE, TO_DATE)).thenReturn(List.of());

        var result = service.progress("user-1", null, FROM_DATE, TO_DATE);

        assertEquals(0, result.resultCount());
        assertTrue(result.payments().isEmpty());
    }

    private PaymentRecord payment(String total, String paid, LocalDate dueDate) {
        PaymentRecord record = new PaymentRecord();
        record.setUserId("user-1");
        record.setBillCategory("Utilities");
        record.setBillName("Test bill");
        record.setTotalAmount(new BigDecimal(total));
        record.setPaidAmount(new BigDecimal(paid));
        record.setFeeAmount(BigDecimal.ZERO);
        record.setDueDate(dueDate);
        record.setCreatedAt(LocalDateTime.of(TODAY, java.time.LocalTime.NOON));
        return record;
    }
}
