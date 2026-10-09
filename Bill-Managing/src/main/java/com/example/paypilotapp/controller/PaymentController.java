package com.example.paypilotapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.paypilotapp.entity.BillPayment;
import com.example.paypilotapp.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }


    // PAY BILL
    @PostMapping("/bill/{billId}")
    public ResponseEntity<BillPayment> makePayment(
            @PathVariable Long billId,
            @RequestBody BillPayment payment) {

        BillPayment result =
                paymentService.makePayment(
                        billId,
                        payment
                );

        return ResponseEntity.ok(result);
    }


    // PAYMENT HISTORY
    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<BillPayment>> getPayments(
            @PathVariable Long billId) {

        return ResponseEntity.ok(
                paymentService.getPayments(billId)
        );
    }
}