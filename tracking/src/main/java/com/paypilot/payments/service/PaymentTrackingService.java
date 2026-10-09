package com.paypilot.payments.service;

import com.paypilot.payments.dto.PaymentHistoryView;
import com.paypilot.payments.dto.PaymentQueryResponse;
import com.paypilot.payments.dto.PaymentStatusView;
import com.paypilot.payments.model.PaymentRecord;
import com.paypilot.payments.model.PaymentStatus;
import com.paypilot.payments.repository.PaymentRecordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentTrackingService {
    private static final Logger log = LogManager.getLogger(PaymentTrackingService.class);

    private final PaymentRecordRepository repository;
    private final int dueSoonDays;
    private final Clock clock;

    @Autowired
    public PaymentTrackingService(PaymentRecordRepository repository,
            @Value("${paypilot.payments.due-soon-days:7}") int dueSoonDays) {
        this(repository, dueSoonDays, Clock.systemDefaultZone());
    }

    // The fixed-clock constructor makes date-dependent behavior deterministic in unit tests.
    PaymentTrackingService(PaymentRecordRepository repository, int dueSoonDays, Clock clock) {
        this.repository = repository;
        this.dueSoonDays = dueSoonDays;
        this.clock = clock;
    }

    public PaymentQueryResponse<PaymentStatusView> progress(String userId, String category,
            LocalDate fromDate, LocalDate toDate) {
        log.debug("Loading payment progress for category={} range={}..{}", category, fromDate, toDate);
        List<PaymentStatusView> rows = findRecords(userId, category, fromDate, toDate)
                .stream().map(this::toStatusView).toList();
        log.info("Payment progress returned {} record(s)", rows.size());
        return new PaymentQueryResponse<>(userId, category, fromDate, toDate, rows.size(), rows);
    }

    public PaymentQueryResponse<PaymentStatusView> overview(String userId, String category,
            LocalDate fromDate, LocalDate toDate) {
        log.debug("Loading payment overview for category={} range={}..{}", category, fromDate, toDate);
        List<PaymentStatusView> rows = findRecords(userId, category, fromDate, toDate)
                .stream().map(this::toStatusView).toList();
        log.info("Payment overview returned {} record(s)", rows.size());
        return new PaymentQueryResponse<>(userId, category, fromDate, toDate, rows.size(), rows);
    }

    public PaymentQueryResponse<PaymentHistoryView> history(String userId, String category,
            LocalDate fromDate, LocalDate toDate) {
        log.debug("Loading payment history for category={} range={}..{}", category, fromDate, toDate);
        List<PaymentHistoryView> rows = findRecords(userId, category, fromDate, toDate).stream()
                .filter(payment -> payment.getPaidAmount().compareTo(payment.getTotalAmount()) >= 0)
                .filter(payment -> payment.getPaidAt() != null)
                .map(PaymentHistoryView::from).toList();
        log.info("Payment history returned {} completed record(s)", rows.size());
        return new PaymentQueryResponse<>(userId, category, fromDate, toDate, rows.size(), rows);
    }

    private List<PaymentRecord> findRecords(String userId, String category,
            LocalDate fromDate, LocalDate toDate) {
        return repository.findForTracking(userId, category, fromDate, toDate);
    }

    private PaymentStatusView toStatusView(PaymentRecord payment) {
        LocalDate today = LocalDate.now(clock);
        BigDecimal total = payment.getTotalAmount();
        BigDecimal paid = payment.getPaidAmount();
        BigDecimal remaining = total.subtract(paid).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        long daysOverdue = payment.getDueDate().isBefore(today) && remaining.signum() > 0
                ? ChronoUnit.DAYS.between(payment.getDueDate(), today) : 0;

        PaymentStatus status;
        if (paid.compareTo(total) >= 0) status = PaymentStatus.PAID;
        else if (paid.signum() > 0) status = PaymentStatus.PARTIALLY_PAID;
        else if (payment.getDueDate().isBefore(today)) status = PaymentStatus.OVERDUE;
        else if (!payment.getDueDate().isAfter(today.plusDays(dueSoonDays))) status = PaymentStatus.DUE_SOON;
        else status = PaymentStatus.PENDING;

        BigDecimal percent = total.signum() == 0 ? new BigDecimal("100.00")
                : paid.multiply(new BigDecimal("100")).divide(total, 2, RoundingMode.HALF_UP)
                        .min(new BigDecimal("100.00"));
        return PaymentStatusView.from(payment, status, daysOverdue, remaining, percent);
    }
}
