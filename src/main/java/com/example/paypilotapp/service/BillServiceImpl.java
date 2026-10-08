package com.example.paypilotapp.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.paypilotapp.entity.Bill;
import com.example.paypilotapp.repository.BillRepository;

@Service
public class BillServiceImpl implements BillService {

    private static final Logger logger =
            LoggerFactory.getLogger(BillServiceImpl.class);

    private final BillRepository billRepository;

    public BillServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public Bill addBill(Bill bill) {

        logger.info("Adding bill");

        if (bill.getStatus() == null ||
                bill.getStatus().isBlank()) {

            bill.setStatus("PENDING");
        }

        if (bill.getRecurringBill() == null ||
                bill.getRecurringBill().isBlank()) {

            bill.setRecurringBill("N");
        }

        Bill savedBill = billRepository.save(bill);

        logger.info("Bill added successfully");

        return savedBill;
    }

    @Override
    public List<Bill> getAllBills(Long userId) {

        logger.info("Getting all bills");

        return billRepository.findByUserId(userId);
    }

    @Override
    public Bill getBillById(Long billId) {

        logger.info("Getting bill");

        return billRepository.findById(billId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Bill not found with ID: " + billId
                        )
                );
    }

    @Override
    public List<Bill> getBillsByCategory(
            Long userId,
            String category) {

        logger.info("Getting bills by category");

        return billRepository.findByUserIdAndBillCategory(
                userId,
                category
        );
    }

    @Override
    public List<Bill> searchBills(
            Long userId,
            String billName) {

        logger.info("Searching bills");

        return billRepository
                .findByUserIdAndBillNameContainingIgnoreCase(
                        userId,
                        billName
                );
    }

    @Override
    public List<Bill> getUpcomingBills(Long userId) {

        logger.info("Getting upcoming bills");

        return billRepository.findUpcomingBills(
                userId,
                LocalDate.now()
        );
    }

    @Override
    public List<Bill> getOverdueBills(Long userId) {

        logger.info("Getting overdue bills");

        return billRepository.findOverdueBills(
                userId,
                LocalDate.now()
        );
    }

    @Override
    public Bill updateBill(
            Long billId,
            Bill newBill) {

        logger.info("Updating bill");

        Bill bill = getBillById(billId);

        bill.setUserId(newBill.getUserId());
        bill.setBillName(newBill.getBillName());
        bill.setBillCategory(newBill.getBillCategory());
        bill.setBillDate(newBill.getBillDate());
        bill.setDueDate(newBill.getDueDate());
        bill.setAmount(newBill.getAmount());
        bill.setReminderFrequency(
                newBill.getReminderFrequency()
        );
        bill.setAttachmentName(
                newBill.getAttachmentName()
        );
        bill.setNotes(newBill.getNotes());
        bill.setRecurringBill(
                newBill.getRecurringBill()
        );

        return billRepository.save(bill);
    }

    @Override
    public void deleteBill(Long billId) {

        logger.info("Deleting bill");

        if (!billRepository.existsById(billId)) {

            throw new RuntimeException(
                    "Bill not found with ID: " + billId
            );
        }

        billRepository.deleteById(billId);

        logger.info("Bill deleted successfully");
    }

    @Override
    public Bill snoozeBill(
            Long billId,
            LocalDate date) {

        logger.info("Snoozing bill");

        Bill bill = getBillById(billId);

        bill.setSnoozedUntil(date);
        bill.setStatus("SNOOZED");

        return billRepository.save(bill);
    }

    @Override
    public Bill markAsPaid(Long billId) {

        logger.info("Marking bill as paid");

        Bill bill = getBillById(billId);

        bill.setStatus("PAID");
        bill.setSnoozedUntil(null);

        return billRepository.save(bill);
    }
}