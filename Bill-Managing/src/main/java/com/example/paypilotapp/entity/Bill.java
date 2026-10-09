package com.example.paypilotapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BILLS")
public class Bill {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_seq")
	@SequenceGenerator(
	    name = "bill_seq",
	    sequenceName = "BILL_SEQ",
	    allocationSize = 1
	)
	@Column(name = "BILL_ID")
	private Long billId;
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "BILL_NAME")
    private String billName;

    @Column(name = "BILL_CATEGORY")
    private String billCategory;

    @Column(name = "BILL_DATE")
    private LocalDate billDate;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "REMINDER_FREQUENCY")
    private String reminderFrequency;

    @Column(name = "ATTACHMENT_NAME")
    private String attachmentName;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "RECURRING_BILL")
    private String recurringBill;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "SNOOZED_UNTIL")
    private LocalDate snoozedUntil;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = "PENDING";
        }

        if (recurringBill == null) {
            recurringBill = "N";
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getBillCategory() {
        return billCategory;
    }

    public void setBillCategory(String billCategory) {
        this.billCategory = billCategory;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReminderFrequency() {
        return reminderFrequency;
    }

    public void setReminderFrequency(String reminderFrequency) {
        this.reminderFrequency = reminderFrequency;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRecurringBill() {
        return recurringBill;
    }

    public void setRecurringBill(String recurringBill) {
        this.recurringBill = recurringBill;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getSnoozedUntil() {
        return snoozedUntil;
    }

    public void setSnoozedUntil(LocalDate snoozedUntil) {
        this.snoozedUntil = snoozedUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}