
package com.example.paypilotapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.paypilotapp.entity.BillReminder;
import com.example.paypilotapp.repository.BillReminderRepository;

@Service
public class ReminderService {

    private final BillReminderRepository billReminderRepository;

    public ReminderService(
            BillReminderRepository billReminderRepository) {
        this.billReminderRepository = billReminderRepository;
    }

    public BillReminder addReminder(BillReminder reminder) {
        return billReminderRepository.save(reminder);
    }

    public List<BillReminder> getReminders(Long billId) {
        return billReminderRepository.findByBillId(billId);
    }

    public BillReminder getReminderById(Long reminderId) {
        return billReminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException(
                        "Reminder not found with ID: " + reminderId));
    }

    public BillReminder updateReminder(
            Long reminderId, BillReminder newReminder) {

        BillReminder reminder = getReminderById(reminderId);

        reminder.setBillId(newReminder.getBillId());
        reminder.setReminderFrequency(
                newReminder.getReminderFrequency());
        reminder.setStartDate(newReminder.getStartDate());
        reminder.setMessage(newReminder.getMessage());
        reminder.setNotificationType(
                newReminder.getNotificationType());
        reminder.setRecurringBill(newReminder.getRecurringBill());

        return billReminderRepository.save(reminder);
    }

    public void deleteReminder(Long reminderId) {
        if (!billReminderRepository.existsById(reminderId)) {
            throw new RuntimeException(
                    "Reminder not found with ID: " + reminderId);
        }

        billReminderRepository.deleteById(reminderId);
    }
}