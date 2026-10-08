package com.example.paypilotapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "BILL_PAYMENT")
public class BillPayment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bill_payment_seq"
        )
        @SequenceGenerator(
            name = "bill_payment_seq",
            sequenceName = "BILL_PAYMENT_SEQ",
            allocationSize = 1
        )
    @Column(name = "PAYMENT_ID")
    private Long paymentId;

    @Column(name = "BILL_ID")
    private Long billId;

    @Column(name = "PAYMENT_AMOUNT")
    private Double paymentAmount;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @Column(name = "PAYMENT_DATE")
    private LocalDate paymentDate;

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;

    @Column(name = "TRANSACTION_REFERENCE")
    private String transactionReference;

    @PrePersist
    public void onCreate() {
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }

        if (paymentStatus == null) {
            paymentStatus = "SUCCESS";
        }
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
}