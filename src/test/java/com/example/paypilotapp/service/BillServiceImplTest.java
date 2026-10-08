package com.example.paypilotapp.service;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.example.paypilotapp.entity.Bill;
import com.example.paypilotapp.repository.BillRepository;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BillServiceImplTest {

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillServiceImpl billService;

    private Bill bill;

    @Before
    public void setUp() {

        bill = new Bill();

        bill.setBillId(1L);
        bill.setUserId(100L);
        bill.setBillName("Electricity Bill");
        bill.setBillCategory("Electricity");
        bill.setBillDate(LocalDate.of(2026, 10, 1));
        bill.setDueDate(LocalDate.of(2026, 10, 15));
        bill.setAmount(1500.0);
        bill.setReminderFrequency("WEEKLY");
        bill.setAttachmentName("electricity.pdf");
        bill.setNotes("Monthly electricity bill");
        bill.setRecurringBill("Y");
        bill.setStatus("PENDING");
    }

    @Test
    public void testBillServiceImpl() {

        assertNotNull(billService);
    }

    @Test
    public void testAddBill() {

        when(billRepository.save(any(Bill.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Bill result = billService.addBill(bill);

        assertNotNull(result);
        assertEquals("Electricity Bill", result.getBillName());
        assertEquals("PENDING", result.getStatus());
        assertEquals("Y", result.getRecurringBill());

        verify(billRepository).save(bill);
    }

    @Test
    public void testGetAllBills() {

        List<Bill> bills = Arrays.asList(bill);

        when(billRepository.findByUserId(100L))
                .thenReturn(bills);

        List<Bill> result =
                billService.getAllBills(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                "Electricity Bill",
                result.get(0).getBillName()
        );

        verify(billRepository)
                .findByUserId(100L);
    }

    @Test
    public void testGetBillById() {

        when(billRepository.findById(1L))
                .thenReturn(Optional.of(bill));

        Bill result =
                billService.getBillById(1L);

        assertNotNull(result);
        assertEquals(
                Long.valueOf(1L),
                result.getBillId()
        );
        assertEquals(
                "Electricity Bill",
                result.getBillName()
        );

        verify(billRepository)
                .findById(1L);
    }

    @Test
    public void testGetBillsByCategory() {

        List<Bill> bills = Arrays.asList(bill);

        when(billRepository.findByUserIdAndBillCategory(
                100L,
                "Electricity"
        )).thenReturn(bills);

        List<Bill> result =
                billService.getBillsByCategory(
                        100L,
                        "Electricity"
                );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                "Electricity",
                result.get(0).getBillCategory()
        );

        verify(billRepository)
                .findByUserIdAndBillCategory(
                        100L,
                        "Electricity"
                );
    }

    @Test
    public void testSearchBills() {

        List<Bill> bills = Arrays.asList(bill);

        when(billRepository
                .findByUserIdAndBillNameContainingIgnoreCase(
                        100L,
                        "Electricity"
                ))
                .thenReturn(bills);

        List<Bill> result =
                billService.searchBills(
                        100L,
                        "Electricity"
                );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                "Electricity Bill",
                result.get(0).getBillName()
        );

        verify(billRepository)
                .findByUserIdAndBillNameContainingIgnoreCase(
                        100L,
                        "Electricity"
                );
    }

    @Test
    public void testGetUpcomingBills() {

        List<Bill> bills = Arrays.asList(bill);

        when(billRepository.findUpcomingBills(
                eq(100L),
                any(LocalDate.class)
        )).thenReturn(bills);

        List<Bill> result =
                billService.getUpcomingBills(100L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(billRepository)
                .findUpcomingBills(
                        eq(100L),
                        any(LocalDate.class)
                );
    }

    @Test
    public void testGetOverdueBills() {

        List<Bill> bills = Arrays.asList(bill);

        when(billRepository.findOverdueBills(
                eq(100L),
                any(LocalDate.class)
        )).thenReturn(bills);

        List<Bill> result =
                billService.getOverdueBills(100L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(billRepository)
                .findOverdueBills(
                        eq(100L),
                        any(LocalDate.class)
                );
    }

    @Test
    public void testUpdateBill() {

        Bill newBill = new Bill();

        newBill.setUserId(100L);
        newBill.setBillName("Updated Electricity Bill");
        newBill.setBillCategory("Electricity");
        newBill.setBillDate(
                LocalDate.of(2026, 10, 1)
        );
        newBill.setDueDate(
                LocalDate.of(2026, 10, 20)
        );
        newBill.setAmount(1800.0);
        newBill.setReminderFrequency("DAILY");
        newBill.setAttachmentName("updated.pdf");
        newBill.setNotes("Updated bill");
        newBill.setRecurringBill("Y");

        when(billRepository.findById(1L))
                .thenReturn(Optional.of(bill));

        when(billRepository.save(any(Bill.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Bill result =
                billService.updateBill(1L, newBill);

        assertNotNull(result);

        assertEquals(
                "Updated Electricity Bill",
                result.getBillName()
        );

        assertEquals(
                Double.valueOf(1800.0),
                result.getAmount()
        );

        assertEquals(
                "DAILY",
                result.getReminderFrequency()
        );

        assertEquals(
                "Updated bill",
                result.getNotes()
        );

        verify(billRepository)
                .findById(1L);

        verify(billRepository)
                .save(bill);
    }

    @Test
    public void testDeleteBill() {

        when(billRepository.existsById(1L))
                .thenReturn(true);

        billService.deleteBill(1L);

        verify(billRepository)
                .existsById(1L);

        verify(billRepository)
                .deleteById(1L);
    }

    @Test
    public void testSnoozeBill() {

        LocalDate snoozeDate =
                LocalDate.of(2026, 10, 20);

        when(billRepository.findById(1L))
                .thenReturn(Optional.of(bill));

        when(billRepository.save(any(Bill.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Bill result =
                billService.snoozeBill(
                        1L,
                        snoozeDate
                );

        assertNotNull(result);

        assertEquals(
                snoozeDate,
                result.getSnoozedUntil()
        );

        assertEquals(
                "SNOOZED",
                result.getStatus()
        );

        verify(billRepository)
                .findById(1L);

        verify(billRepository)
                .save(bill);
    }

    @Test
    public void testMarkAsPaid() {

        when(billRepository.findById(1L))
                .thenReturn(Optional.of(bill));

        when(billRepository.save(any(Bill.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Bill result =
                billService.markAsPaid(1L);

        assertNotNull(result);

        assertEquals(
                "PAID",
                result.getStatus()
        );

        assertNull(
                result.getSnoozedUntil()
        );

        verify(billRepository)
                .findById(1L);

        verify(billRepository)
                .save(bill);
    }
}