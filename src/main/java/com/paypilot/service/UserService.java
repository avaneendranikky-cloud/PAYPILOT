package com.paypilot.service;

import com.paypilot.controller.ForgotPasswordRequest;
import com.paypilot.controller.LoginRequest;
import com.paypilot.controller.RegisterRequest;
import com.paypilot.controller.ResetPasswordRequest;

import com.paypilot.entity.User;
import com.paypilot.repository.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger =
            LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserService(
            UserRepository userRepository,
            OtpService otpService) {

        this.userRepository = userRepository;
        this.otpService = otpService;
        this.passwordEncoder =
                new BCryptPasswordEncoder();

        logger.info("UserService initialized");
    }


    // =====================================================
    // REGISTER USER
    // =====================================================

    public User registerUser(RegisterRequest request) {

        logger.info(
                "Registration request received for user: {}",
                request.getUserId()
        );


        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException(
                    "Password and Confirm Password do not match"
            );
        }


        if (userRepository.existsByUserId(
                request.getUserId())) {

            throw new RuntimeException(
                    "User ID already exists"
            );
        }


        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }


        if (userRepository.existsByPanNumber(
                request.getPanNumber())) {

            throw new RuntimeException(
                    "PAN number is already registered"
            );
        }


        if (userRepository.existsByBankAccountNumber(
                request.getBankAccountNumber())) {

            throw new RuntimeException(
                    "Bank account is already registered"
            );
        }


        User user = new User();

        user.setUserId(request.getUserId());

        user.setEmail(request.getEmail());

        user.setEmailVerified(false);

        user.setPanNumber(request.getPanNumber());

        user.setBankAccountNumber(
                request.getBankAccountNumber()
        );

        user.setIfscCode(
                request.getIfscCode()
        );

        user.setBankingPartner(
                request.getBankingPartner()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setFailedAttempts(0);

        user.setAccountLocked(false);


        User savedUser =
                userRepository.save(user);


        // Generate and send OTP
        otpService.generateAndSendOtp(
                request.getEmail()
        );


        logger.info(
                "User registered and OTP sent: {}",
                savedUser.getUserId()
        );


        return savedUser;
    }


    // =====================================================
    // VERIFY REGISTRATION OTP
    // =====================================================

    public void verifyRegistrationOtp(
            String email,
            String otp) {

        otpService.verifyOtp(email, otp);


        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));


        user.setEmailVerified(true);

        userRepository.save(user);


        logger.info(
                "Registration OTP verified for: {}",
                email
        );
    }


    // =====================================================
    // LOGIN
    // =====================================================

    public User login(LoginRequest request) {

        logger.info(
                "Login request received for user: {}",
                request.getUserId()
        );


        User user = userRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid User ID or Password"
                        ));


        if (!user.isEmailVerified()) {

            throw new RuntimeException(
                    "Please verify your email using OTP"
            );
        }


        if (user.isAccountLocked()) {

            throw new RuntimeException(
                    "Account is locked. Please contact support."
            );
        }


        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {


            user.setFailedAttempts(
                    user.getFailedAttempts() + 1
            );


            if (user.getFailedAttempts() >= 3) {

                user.setAccountLocked(true);

                userRepository.save(user);


                throw new RuntimeException(
                        "Account locked after 3 failed login attempts"
                );
            }


            userRepository.save(user);


            throw new RuntimeException(
                    "Invalid User ID or Password. Attempt "
                    + user.getFailedAttempts()
                    + " of 3"
            );
        }


        user.setFailedAttempts(0);

        userRepository.save(user);


        logger.info(
                "Login successful for user: {}",
                request.getUserId()
        );


        return user;
    }


    // =====================================================
    // FORGOT PASSWORD - SEND OTP
    // =====================================================

    public void forgotPassword(
            ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));


        otpService.generateAndSendOtp(
                user.getEmail()
        );


        logger.info(
                "Forgot password OTP sent to: {}",
                user.getEmail()
        );
    }


    // =====================================================
    // VERIFY FORGOT PASSWORD OTP
    // =====================================================

    public void verifyForgotPasswordOtp(
            String email,
            String otp) {

        otpService.verifyOtp(email, otp);

        logger.info(
                "Forgot password OTP verified for: {}",
                email
        );
    }


    // =====================================================
    // RESET PASSWORD
    // =====================================================

    public String resetPassword(
            ResetPasswordRequest request) {


        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));


        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException(
                    "Password and Confirm Password do not match"
            );
        }


        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );


        // Unlock account
        user.setFailedAttempts(0);

        user.setAccountLocked(false);


        userRepository.save(user);


        logger.info(
                "Password reset and account unlocked for: {}",
                request.getEmail()
        );


        return "Password reset successful. Account unlocked.";
    }


    // =====================================================
    // GET USER
    // =====================================================

    public User getUserByUserId(String userId) {

        return userRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }
}