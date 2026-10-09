package com.paypilot.controller;

import com.paypilot.entity.ScheduledPayment;
import com.paypilot.service.ScheduledPaymentService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduled-payments")
public class ScheduledPaymentController {

    private static final Logger logger =
            LogManager.getLogger(
                    ScheduledPaymentController.class);

    private final ScheduledPaymentService service;

    public ScheduledPaymentController(
            ScheduledPaymentService service) {

        this.service = service;
    }

    // Schedule payment

    @PostMapping
    public ResponseEntity<ScheduledPayment> schedulePayment(
            @RequestBody ScheduledPayment payment) {

        logger.info(
                "Received request to schedule payment for bill ID: {}",
                payment.getBillId());

        return ResponseEntity.ok(
                service.schedulePayment(payment));
    }

    // Get payment by ID

    @GetMapping("/{id}")
    public ResponseEntity<ScheduledPayment>
    getScheduledPayment(
            @PathVariable Integer id) {

        logger.info(
                "Received request to fetch scheduled payment with ID: {}",
                id);

        return ResponseEntity.ok(
                service.getScheduledPayment(id));
    }

    // Get all payments

    @GetMapping
    public ResponseEntity<List<ScheduledPayment>>
    getAllPayments() {

        logger.info(
                "Received request to fetch all scheduled payments");

        return ResponseEntity.ok(
                service.getAllPayments());
    }

    // Update payment

    @PutMapping("/{id}")
    public ResponseEntity<ScheduledPayment>
    updateScheduledPayment(
            @PathVariable Integer id,
            @RequestBody ScheduledPayment payment) {

        logger.info(
                "Received request to update scheduled payment with ID: {}",
                id);

        return ResponseEntity.ok(
                service.updateScheduledPayment(
                        id,
                        payment));
    }

    // Cancel payment

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ScheduledPayment>
    cancelScheduledPayment(
            @PathVariable Integer id) {

        logger.info(
                "Received request to cancel scheduled payment with ID: {}",
                id);

        return ResponseEntity.ok(
                service.cancelScheduledPayment(id));
    }
}