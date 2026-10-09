package com.example.paypilotapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.paypilotapp.entity.Bill;
import com.example.paypilotapp.service.BillService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:4200")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }


    // ADD NEW BILL
    @PostMapping
    public ResponseEntity<Bill> addBill(
            @RequestBody Bill bill) {

        Bill savedBill =
                billService.addBill(bill);

        return ResponseEntity.ok(savedBill);
    }


    // GET ALL BILLS
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bill>> getAllBills(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                billService.getAllBills(userId)
        );
    }


    // GET BILL BY ID
    @GetMapping("/{billId}")
    public ResponseEntity<Bill> getBillById(
            @PathVariable Long billId) {

        return ResponseEntity.ok(
                billService.getBillById(billId)
        );
    }


    // FILTER BY CATEGORY
    @GetMapping("/user/{userId}/category")
    public ResponseEntity<List<Bill>> getByCategory(
            @PathVariable Long userId,
            @RequestParam String category) {

        return ResponseEntity.ok(
                billService.getBillsByCategory(
                        userId,
                        category
                )
        );
    }


    // SEARCH BY BILL NAME
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<Bill>> searchBills(
            @PathVariable Long userId,
            @RequestParam String billName) {

        return ResponseEntity.ok(
                billService.searchBills(
                        userId,
                        billName
                )
        );
    }


    // UPCOMING BILLS
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<Bill>> getUpcomingBills(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                billService.getUpcomingBills(userId)
        );
    }


    // OVERDUE BILLS
    @GetMapping("/user/{userId}/overdue")
    public ResponseEntity<List<Bill>> getOverdueBills(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                billService.getOverdueBills(userId)
        );
    }


    // UPDATE BILL
    @PutMapping("/{billId}")
    public ResponseEntity<Bill> updateBill(
            @PathVariable Long billId,
            @RequestBody Bill bill) {

        return ResponseEntity.ok(
                billService.updateBill(
                        billId,
                        bill
                )
        );
    }


    // DELETE BILL
    @DeleteMapping("/{billId}")
    public ResponseEntity<String> deleteBill(
            @PathVariable Long billId) {

        billService.deleteBill(billId);

        return ResponseEntity.ok(
                "Bill deleted successfully"
        );
    }


    // SNOOZE BILL
    @PutMapping("/{billId}/snooze")
    public ResponseEntity<Bill> snoozeBill(
            @PathVariable Long billId,
            @RequestParam String date) {

        LocalDate snoozeDate =
                LocalDate.parse(date);

        return ResponseEntity.ok(
                billService.snoozeBill(
                        billId,
                        snoozeDate
                )
        );
    }


    // MARK BILL AS PAID
    @PutMapping("/{billId}/paid")
    public ResponseEntity<Bill> markAsPaid(
            @PathVariable Long billId) {

        return ResponseEntity.ok(
                billService.markAsPaid(billId)
        );
    }
}