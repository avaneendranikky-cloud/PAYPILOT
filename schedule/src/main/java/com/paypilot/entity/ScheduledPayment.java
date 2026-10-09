package com.paypilot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "SCHEDULED_PAYMENTS")
public class ScheduledPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduledPaymentId;

    private Integer billId;

    private LocalDate paymentDate;

    private String amountFrequency;

    private boolean isActive;

    public Integer getScheduledPaymentId() {
        return scheduledPaymentId;
    }

    public void setScheduledPaymentId(Integer scheduledPaymentId) {
        this.scheduledPaymentId = scheduledPaymentId;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getAmountFrequency() {
        return amountFrequency;
    }

    public void setAmountFrequency(String amountFrequency) {
        this.amountFrequency = amountFrequency;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}