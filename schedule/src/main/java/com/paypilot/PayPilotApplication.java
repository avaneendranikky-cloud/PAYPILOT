package com.paypilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayPilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                PayPilotApplication.class,
                args);
        System.out.println("===================");
        System.out.println("Application started");
        System.out.println("===================");
    }
}