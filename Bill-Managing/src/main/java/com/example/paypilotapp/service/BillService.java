package com.example.paypilotapp.service;

import java.time.LocalDate;
import java.util.List;

import com.example.paypilotapp.entity.Bill;

public interface BillService {

    Bill addBill(Bill bill);

    List<Bill> getAllBills(Long userId);

    Bill getBillById(Long billId);

    List<Bill> getBillsByCategory(
            Long userId,
            String category
    );

    List<Bill> searchBills(
            Long userId,
            String billName
    );

    List<Bill> getUpcomingBills(Long userId);

    List<Bill> getOverdueBills(Long userId);

    Bill updateBill(
            Long billId,
            Bill bill
    );

    void deleteBill(Long billId);

    Bill snoozeBill(
            Long billId,
            LocalDate date
    );

    Bill markAsPaid(Long billId);
}