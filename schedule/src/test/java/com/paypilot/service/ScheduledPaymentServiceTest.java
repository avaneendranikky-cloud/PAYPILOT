package com.paypilot.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.paypilot.entity.ScheduledPayment;
import com.paypilot.repository.ScheduledPaymentRepository;

public class ScheduledPaymentServiceTest {

    @Mock
    private ScheduledPaymentRepository repository;

    @InjectMocks
    private ScheduledPaymentService service;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testScheduledPaymentService() {

        assertNotNull(service);
    }

    @Test
    public void testSchedulePayment() {

        ScheduledPayment payment=new ScheduledPayment();

        payment.setBillId(101);

        payment.setPaymentDate(
                LocalDate.of(2026, 10, 15));

        payment.setAmountFrequency(
                "MONTHLY");

        org.mockito.Mockito.when(
                repository.save(payment))
                .thenReturn(payment);

        ScheduledPayment result =
                service.schedulePayment(payment);

        assertNotNull(result);

        assertEquals(
                Integer.valueOf(101),
                result.getBillId());

        assertEquals(
                LocalDate.of(2026, 10, 15),
                result.getPaymentDate());

        assertEquals(
                "MONTHLY",
                result.getAmountFrequency());

        assertTrue(result.isActive());

        org.mockito.Mockito.verify(
                repository,
                org.mockito.Mockito.times(1))
                .save(payment);
    }

    @Test
    public void testGetScheduledPayment() {

        ScheduledPayment payment =
                new ScheduledPayment();

        payment.setScheduledPaymentId(1);
        payment.setBillId(101);

        org.mockito.Mockito.when(
                repository.findById(1))
                .thenReturn(Optional.of(payment));

        ScheduledPayment result =
                service.getScheduledPayment(1);

        assertNotNull(result);

        assertEquals(
                Integer.valueOf(1),
                result.getScheduledPaymentId());

        assertEquals(
                Integer.valueOf(101),
                result.getBillId());

        org.mockito.Mockito.verify(
                repository,
                org.mockito.Mockito.times(1))
                .findById(1);
    }

    @Test
    public void testGetAllPayments() {

        ScheduledPayment payment1 =
                new ScheduledPayment();

        payment1.setScheduledPaymentId(1);

        ScheduledPayment payment2 =
                new ScheduledPayment();

        payment2.setScheduledPaymentId(2);

        List<ScheduledPayment> payments =
                Arrays.asList(payment1, payment2);

        org.mockito.Mockito.when(
                repository.findAll())
                .thenReturn(payments);

        List<ScheduledPayment> result =
                service.getAllPayments();

        assertNotNull(result);

        assertEquals(
                2,
                result.size());

        assertEquals(
                Integer.valueOf(1),
                result.get(0).getScheduledPaymentId());

        assertEquals(
                Integer.valueOf(2),
                result.get(1).getScheduledPaymentId());

        org.mockito.Mockito.verify(
                repository,
                org.mockito.Mockito.times(1))
                .findAll();
    }

    @Test
    public void testUpdateScheduledPayment() {

        ScheduledPayment existing =
                new ScheduledPayment();

        existing.setScheduledPaymentId(1);
        existing.setBillId(101);
        existing.setAmountFrequency("MONTHLY");

        ScheduledPayment updated =
                new ScheduledPayment();

        updated.setBillId(102);

        updated.setPaymentDate(
                LocalDate.of(2026, 10, 20));

        updated.setAmountFrequency(
                "WEEKLY");

        org.mockito.Mockito.when(
                repository.findById(1))
                .thenReturn(Optional.of(existing));

        org.mockito.Mockito.when(
                repository.save(existing))
                .thenReturn(existing);

        ScheduledPayment result =
                service.updateScheduledPayment(
                        1,
                        updated);

        assertNotNull(result);

        assertEquals(
                Integer.valueOf(102),
                result.getBillId());

        assertEquals(
                LocalDate.of(2026, 10, 20),
                result.getPaymentDate());

        assertEquals(
                "WEEKLY",
                result.getAmountFrequency());

        org.mockito.Mockito.verify(
                repository,
                org.mockito.Mockito.times(1))
                .findById(1);

        org.mockito.Mockito.verify(
                repository,
                org.mockito.Mockito.times(1))
                .save(existing);
    }

    @Test
    public void testCancelScheduledPayment() {

        ScheduledPayment payment =
                new ScheduledPayment();

        payment.setScheduledPaymentId(1);
        payment.setBillId(101);
        payment.setActive(true);

        org.mockito.Mockito.when(
                repository.findById(1))
                .thenReturn(Optional.of(payment));

        org.mockito.Mockito.when(
                repository.save(payment))
                .thenReturn(payment);

        ScheduledPayment result =
                service.cancelScheduledPayment(1);

        assertNotNull(result);

        assertFalse(result.isActive());

        org.mockito.Mockito.verify(
                repository,
                org.mockito.Mockito.times(1))
                .findById(1);

        org.mockito.Mockito.verify(
                repository,
                org.mockito.Mockito.times(1))
                .save(payment);
    }
}