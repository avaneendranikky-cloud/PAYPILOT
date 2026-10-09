package com.paypilot.service;

import com.paypilot.entity.Otp;
import com.paypilot.repository.OtpRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public OtpService(
            OtpRepository otpRepository,
            EmailService emailService) {

        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }


    // ==========================================
    // GENERATE AND SEND OTP
    // ==========================================

    public void generateAndSendOtp(String email) {

        String otpValue = String.format(
                "%06d",
                new Random().nextInt(1000000)
        );

        Otp otp = new Otp();

        otp.setEmail(email);
        otp.setOtp(otpValue);

        otp.setExpiryTime(
                LocalDateTime.now().plusMinutes(5)
        );

        otp.setVerified(false);

        otpRepository.save(otp);

        emailService.sendOtp(email, otpValue);
    }


    // ==========================================
    // VERIFY OTP
    // ==========================================

    public void verifyOtp(
            String email,
            String enteredOtp) {

        Otp otp = otpRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() ->
                        new RuntimeException("OTP not found")
                );

        if (otp.isVerified()) {

            throw new RuntimeException(
                    "OTP already used"
            );
        }

        if (LocalDateTime.now()
                .isAfter(otp.getExpiryTime())) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }

        if (!otp.getOtp().equals(enteredOtp)) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        otp.setVerified(true);

        otpRepository.save(otp);
    }
}