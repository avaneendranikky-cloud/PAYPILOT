
package com.example.paypilotapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.paypilotapp.entity.BillReminder;
import com.example.paypilotapp.service.ReminderService;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "http://localhost:4200")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<BillReminder> addReminder(
            @RequestBody BillReminder reminder) {
        return ResponseEntity.ok(
                reminderService.addReminder(reminder));
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<BillReminder>> getReminders(
            @PathVariable Long billId) {
        return ResponseEntity.ok(
                reminderService.getReminders(billId));
    }

    @GetMapping("/{reminderId}")
    public ResponseEntity<BillReminder> getReminderById(
            @PathVariable Long reminderId) {
        return ResponseEntity.ok(
                reminderService.getReminderById(reminderId));
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<BillReminder> updateReminder(
            @PathVariable Long reminderId,
            @RequestBody BillReminder reminder) {
        return ResponseEntity.ok(
                reminderService.updateReminder(
                        reminderId, reminder));
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<String> deleteReminder(
            @PathVariable Long reminderId) {
        reminderService.deleteReminder(reminderId);
        return ResponseEntity.ok("Reminder deleted successfully");
    }
}