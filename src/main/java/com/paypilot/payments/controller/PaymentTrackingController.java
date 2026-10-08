package com.paypilot.payments.controller;

import com.paypilot.payments.dto.PaymentHistoryView;
import com.paypilot.payments.dto.PaymentQueryResponse;
import com.paypilot.payments.dto.PaymentStatusView;
import com.paypilot.payments.service.PaymentTrackingService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/users/{userId}/payments")
@Validated
public class PaymentTrackingController {
    private static final Logger log = LogManager.getLogger(PaymentTrackingController.class);
    private final PaymentTrackingService service;

    public PaymentTrackingController(PaymentTrackingService service) { this.service = service; }

    @GetMapping("/progress")
    public PaymentQueryResponse<PaymentStatusView> progress(
            @PathVariable @NotBlank @Size(max = 100) String userId,
            @RequestParam(required = false) @Size(max = 60) String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        log.info("GET payment progress request received");
        return service.progress(userId, normalize(category), fromDate, toDate);
    }

    @GetMapping("/overview")
    public PaymentQueryResponse<PaymentStatusView> overview(
            @PathVariable @NotBlank @Size(max = 100) String userId,
            @RequestParam(required = false) @Size(max = 60) String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        log.info("GET payment overview request received");
        return service.overview(userId, normalize(category), fromDate, toDate);
    }

    @GetMapping("/history")
    public PaymentQueryResponse<PaymentHistoryView> history(
            @PathVariable @NotBlank @Size(max = 100) String userId,
            @RequestParam(required = false) @Size(max = 60) String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        log.info("GET payment history request received");
        return service.history(userId, normalize(category), fromDate, toDate);
    }

    private String normalize(String category) {
        return category == null || category.isBlank() ? null : category.trim();
    }
}
