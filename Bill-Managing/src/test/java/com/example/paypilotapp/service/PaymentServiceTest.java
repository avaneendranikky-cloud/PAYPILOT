package com.example.paypilotapp.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.example.paypilotapp.entity.Bill;
import com.example.paypilotapp.entity.BillPayment;
import com.example.paypilotapp.repository.BillPaymentRepository;
import com.example.paypilotapp.repository.BillRepository;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @Mock
    private BillPaymentRepository billPaymentRepository;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Bill bill;
    private BillPayment payment;

    @Before
    public void setUp() {

        bill = new Bill();
        bill.setBillId(1L);
        bill.setBillName("Electricity Bill");
        bill.setAmount(1500.0);
        bill.setStatus("PENDING");

        payment = new BillPayment();
        payment.setPaymentAmount(1500.0);
        payment.setPaymentMethod("UPI");
    }

    @Test
    public void testPaymentService() {

        assertNotNull(paymentService);
    }

    @Test
    public void testMakePayment() {

        when(billRepository.findById(1L))
                .thenReturn(java.util.Optional.of(bill));

        when(billPaymentRepository.save(any(BillPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BillPayment result =
                paymentService.makePayment(1L, payment);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getBillId());
        assertEquals("SUCCESS", result.getPaymentStatus());
        assertNotNull(result.getPaymentDate());
        assertNotNull(result.getTransactionReference());

        assertEquals("PAID", bill.getStatus());

        verify(billRepository).findById(1L);
        verify(billPaymentRepository).save(payment);
        verify(billRepository).save(bill);
    }

    @Test
    public void testGetPayments() {

        BillPayment payment1 = new BillPayment();
        payment1.setPaymentId(1L);
        payment1.setBillId(1L);
        payment1.setPaymentAmount(1000.0);

        BillPayment payment2 = new BillPayment();
        payment2.setPaymentId(2L);
        payment2.setBillId(1L);
        payment2.setPaymentAmount(500.0);

        List<BillPayment> payments =
                java.util.Arrays.asList(payment1, payment2);

        when(billPaymentRepository.findByBillId(1L))
                .thenReturn(payments);

        List<BillPayment> result =
                paymentService.getPayments(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(1L),
                result.get(0).getPaymentId());

        verify(billPaymentRepository)
                .findByBillId(1L);
    }
}