package com.example.paypilotapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.paypilotapp.entity.BillReminder;

import java.util.List;

public interface BillReminderRepository
        extends JpaRepository<BillReminder, Long> {

    List<BillReminder> findByBillId(Long billId);
}