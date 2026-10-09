package com.paypilot.payments.dto;
import com.paypilot.payments.model.PaymentRecord;
import java.math.BigDecimal;
import java.time.*;
public record PaymentHistoryView(Long paymentId,String billName,String billCategory,BigDecimal amountPaid,
 LocalDate dueDate,LocalDateTime paidAt,String paymentMethod) {
 public static PaymentHistoryView from(PaymentRecord p){return new PaymentHistoryView(p.getId(),p.getBillName(),p.getBillCategory(),p.getPaidAmount(),p.getDueDate(),p.getPaidAt(),p.getPaymentMethod());}
}
