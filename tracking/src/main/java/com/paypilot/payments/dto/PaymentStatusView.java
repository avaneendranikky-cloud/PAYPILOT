package com.paypilot.payments.dto;
import com.paypilot.payments.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record PaymentStatusView(Long paymentId,String billName,String billCategory,BigDecimal totalAmount,
 BigDecimal amountPaid,BigDecimal amountRemaining,BigDecimal feeAmount,LocalDate dueDate,Long daysOverdue,
 PaymentStatus status,BigDecimal progressPercent) {
 public static PaymentStatusView from(PaymentRecord p,PaymentStatus s,long overdue,BigDecimal remaining,BigDecimal percent){
  return new PaymentStatusView(p.getId(),p.getBillName(),p.getBillCategory(),p.getTotalAmount(),p.getPaidAmount(),remaining,p.getFeeAmount(),p.getDueDate(),overdue,s,percent);
 }
}
