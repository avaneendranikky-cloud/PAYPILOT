package com.paypilot.service;

import com.paypilot.entity.ScheduledPayment;
import com.paypilot.repository.ScheduledPaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduledPaymentService {

    private static final Logger logger =
            LoggerFactory.getLogger(ScheduledPaymentService.class);

    private final ScheduledPaymentRepository repository;

    public ScheduledPaymentService(
            ScheduledPaymentRepository repository) {

        this.repository = repository;
    }

    // Schedule a new payment

    public ScheduledPayment schedulePayment(
            ScheduledPayment payment) {

        logger.info("Scheduling payment for bill ID: {}",
                payment.getBillId());

        payment.setActive(true);

        ScheduledPayment savedPayment =
                repository.save(payment);

        logger.info("Payment scheduled successfully with ID: {}",
                savedPayment.getScheduledPaymentId());

        return savedPayment;
    }

    // Get scheduled payment by ID

    public ScheduledPayment getScheduledPayment(
            Integer id) {

        logger.info("Fetching scheduled payment with ID: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> {

                    logger.error(
                            "Scheduled payment not found with ID: {}",
                            id);

                    return new RuntimeException(
            "Scheduled payment not found");
                });
    }

    // Get all scheduled payments

    public List<ScheduledPayment> getAllPayments() {

        logger.info("Fetching all scheduled payments");

        List<ScheduledPayment> payments =
                repository.findAll();

        logger.info("Total scheduled payments found: {}",
                payments.size());

        return payments;
    }

    // Update scheduled payment

    public ScheduledPayment updateScheduledPayment(
            Integer id,
            ScheduledPayment updatedPayment) {

        logger.info(
                "Updating scheduled payment with ID: {}",
                id);

        ScheduledPayment existing =
                repository.findById(id)
                        .orElseThrow(() -> {

                            logger.error(
                                    "Scheduled payment not found with ID: {}",
                                    id);

                            return new RuntimeException(
                                    "Scheduled payment not found");
                        });

        existing.setBillId(
                updatedPayment.getBillId());

        existing.setPaymentDate(
                updatedPayment.getPaymentDate());

        existing.setAmountFrequency(
                updatedPayment.getAmountFrequency());

        ScheduledPayment savedPayment =
                repository.save(existing);

        logger.info(
                "Scheduled payment updated successfully with ID: {}",
                id);

        return savedPayment;
    }

    // Cancel scheduled payment

    public ScheduledPayment cancelScheduledPayment(
            Integer id) {

        logger.info(
                "Cancelling scheduled payment with ID: {}",
                id);

        ScheduledPayment existing =
                repository.findById(id)
                        .orElseThrow(() -> {

                            logger.error(
                                    "Scheduled payment not found with ID: {}",
                                    id);

                            return new RuntimeException(
                                    "Scheduled payment not found");
                        });

        existing.setActive(false);

        ScheduledPayment cancelledPayment =
                repository.save(existing);

        logger.info(
                "Scheduled payment cancelled successfully with ID: {}",
                id);

        return cancelledPayment;
    }
}