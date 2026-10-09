package com.example.paypilotapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "BILL_REMINDER")
public class BillReminder {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bill_reminder_seq"
    )
    @SequenceGenerator(
            name = "bill_reminder_seq",
            sequenceName = "BILL_REMINDER_SEQ",
            allocationSize = 1
    )
    @Column(name = "REMINDER_ID")
    private Long reminderId;

    @Column(name = "BILL_ID")
    private Long billId;

    @Column(name = "REMINDER_FREQUENCY")
    private String reminderFrequency;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "NOTIFICATION_TYPE")
    private String notificationType;

    @Column(name = "RECURRING_BILL")
    private String recurringBill;

    public Long getReminderId() {
        return reminderId;
    }

    public void setReminderId(Long reminderId) {
        this.reminderId = reminderId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getReminderFrequency() {
        return reminderFrequency;
    }

    public void setReminderFrequency(String reminderFrequency) {
        this.reminderFrequency = reminderFrequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getRecurringBill() {
        return recurringBill;
    }

    public void setRecurringBill(String recurringBill) {
        this.recurringBill = recurringBill;
    }
}