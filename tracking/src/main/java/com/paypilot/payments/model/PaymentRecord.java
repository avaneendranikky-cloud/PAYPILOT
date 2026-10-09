package com.paypilot.payments.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
/** One bill/payment obligation belonging to a user. */
@Entity @Table(name="PAYMENT_RECORD")
public class PaymentRecord {
 @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="payment_record_seq")
 @SequenceGenerator(name="payment_record_seq",sequenceName="PAYMENT_RECORD_SEQ",allocationSize=1)
 private Long id;
 @Column(name="USER_ID",nullable=false,length=100) private String userId;
 @Column(name="BILL_CATEGORY",nullable=false,length=60) private String billCategory;
 @Column(name="BILL_NAME",nullable=false,length=160) private String billName;
 @Column(name="TOTAL_AMOUNT",nullable=false,precision=19,scale=2) private BigDecimal totalAmount;
 @Column(name="PAID_AMOUNT",nullable=false,precision=19,scale=2) private BigDecimal paidAmount=BigDecimal.ZERO;
 @Column(name="FEE_AMOUNT",nullable=false,precision=19,scale=2) private BigDecimal feeAmount=BigDecimal.ZERO;
 @Column(name="DUE_DATE",nullable=false) private LocalDate dueDate;
 @Column(name="PAID_AT") private LocalDateTime paidAt;
 @Column(name="PAYMENT_METHOD",length=60) private String paymentMethod;
 @Column(name="CREATED_AT",nullable=false) private LocalDateTime createdAt;
 public PaymentRecord() {}
 public Long getId(){return id;} public String getUserId(){return userId;} public String getBillCategory(){return billCategory;}
 public String getBillName(){return billName;} public BigDecimal getTotalAmount(){return totalAmount;}
 public BigDecimal getPaidAmount(){return paidAmount;} public BigDecimal getFeeAmount(){return feeAmount;}
 public LocalDate getDueDate(){return dueDate;} public LocalDateTime getPaidAt(){return paidAt;}
 public String getPaymentMethod(){return paymentMethod;} public LocalDateTime getCreatedAt(){return createdAt;}
 public void setId(Long v){id=v;} public void setUserId(String v){userId=v;} public void setBillCategory(String v){billCategory=v;}
 public void setBillName(String v){billName=v;} public void setTotalAmount(BigDecimal v){totalAmount=v;}
 public void setPaidAmount(BigDecimal v){paidAmount=v;} public void setFeeAmount(BigDecimal v){feeAmount=v;}
 public void setDueDate(LocalDate v){dueDate=v;} public void setPaidAt(LocalDateTime v){paidAt=v;}
 public void setPaymentMethod(String v){paymentMethod=v;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
