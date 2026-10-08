package com.paypilot.payments.dto;
import java.time.LocalDate;
import java.util.List;
public record PaymentQueryResponse<T>(String userId,String category,LocalDate fromDate,LocalDate toDate,int resultCount,List<T> payments){}
