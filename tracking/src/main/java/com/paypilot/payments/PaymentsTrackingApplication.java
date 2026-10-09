package com.paypilot.payments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@SpringBootApplication
public class PaymentsTrackingApplication {
 private static final Logger log = LogManager.getLogger(PaymentsTrackingApplication.class);
 public static void main(String[] args) {
	 SpringApplication.run(PaymentsTrackingApplication.class, args);
	 log.info("==================================================");
         log.info("^_^ Starting PayPilot payment tracking backend ^_^");
     log.info("==================================================");    
  
 }
}
