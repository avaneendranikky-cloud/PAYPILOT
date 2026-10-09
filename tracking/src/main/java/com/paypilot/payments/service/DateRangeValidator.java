package com.paypilot.payments.service;
import com.paypilot.payments.exception.InvalidDateRangeException;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.LocalDate;
@Component public class DateRangeValidator implements HandlerInterceptor {
 @Override public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler){
  String from=req.getParameter("fromDate"),to=req.getParameter("toDate");
  if(from==null||to==null)throw new InvalidDateRangeException("fromDate and toDate are required.");
  try{if(LocalDate.parse(from).isAfter(LocalDate.parse(to)))throw new InvalidDateRangeException("fromDate must be on or before toDate.");}
  catch(java.time.format.DateTimeParseException e){throw new InvalidDateRangeException("Dates must use ISO format YYYY-MM-DD.");}
  return true;
 }
}
