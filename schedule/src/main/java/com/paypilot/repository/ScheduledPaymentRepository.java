package com.paypilot.repository;

import com.paypilot.entity.ScheduledPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledPaymentRepository
        extends JpaRepository<ScheduledPayment, Integer> {

}