package com.example.paypilotapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.paypilotapp.entity.BillPayment;

import java.util.List;

public interface BillPaymentRepository
        extends JpaRepository<BillPayment, Long> {

    List<BillPayment> findByBillId(Long billId);
}