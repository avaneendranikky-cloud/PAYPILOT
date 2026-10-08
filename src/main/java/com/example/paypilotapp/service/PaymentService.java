package com.example.paypilotapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.paypilotapp.entity.Bill;
import com.example.paypilotapp.entity.BillPayment;
import com.example.paypilotapp.repository.BillPaymentRepository;
import com.example.paypilotapp.repository.BillRepository;

@Service
public class PaymentService {

    private static final Logger logger =
            LoggerFactory.getLogger(PaymentService.class);

    private final BillPaymentRepository billPaymentRepository;

    private final BillRepository billRepository;

    public PaymentService(
            BillPaymentRepository billPaymentRepository,
            BillRepository billRepository) {

        this.billPaymentRepository = billPaymentRepository;
        this.billRepository = billRepository;

        logger.info("PaymentService initialized");
    }

    public BillPayment makePayment(
            Long billId,
            BillPayment payment) {

        logger.info(
                "Processing payment for bill ID: {}",
                billId
        );

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> {

                    logger.error(
                            "Bill not found with ID: {}",
                            billId
                    );

                    return new RuntimeException(
                            "Bill not found with ID: " + billId
                    );
                });

        payment.setBillId(billId);

        payment.setPaymentDate(
                LocalDate.now()
        );

        payment.setPaymentStatus(
                "SUCCESS"
        );

        String transactionReference =
                "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        payment.setTransactionReference(
                transactionReference
        );

        logger.info(
                "Payment details prepared. Amount: {}, Method: {}",
                payment.getPaymentAmount(),
                payment.getPaymentMethod()
        );

        BillPayment savedPayment =
                billPaymentRepository.save(payment);

        logger.info(
                "Payment saved successfully. Payment ID: {}",
                savedPayment.getPaymentId()
        );

        bill.setStatus("PAID");

        billRepository.save(bill);

        logger.info(
                "Bill ID {} marked as PAID",
                billId
        );

        return savedPayment;
    }

    public List<BillPayment> getPayments(
            Long billId) {

        logger.info(
                "Getting payments for bill ID: {}",
                billId
        );

        List<BillPayment> payments =
                billPaymentRepository
                        .findByBillId(billId);

        logger.info(
                "Payments found: {}",
                payments.size()
        );

        return payments;
    }
}