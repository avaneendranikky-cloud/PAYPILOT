package com.paypilot.controller;

import com.paypilot.entity.User;
import com.paypilot.service.UserService;

import jakarta.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private static final Logger logger =
            LogManager.getLogger(UserController.class);

    private final UserService userService;


    public UserController(UserService userService) {

        this.userService = userService;

        logger.info("UserController initialized");
    }


    // =====================================================
    // REGISTER
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        try {

            User user =
                    userService.registerUser(request);


            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(user);

        } catch (RuntimeException e) {

            logger.error(
                    "Registration failed: {}",
                    e.getMessage()
            );


            return ResponseEntity
                    .badRequest()
                    .body(
                            new ErrorResponse(
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // VERIFY REGISTRATION OTP
    // =====================================================

    @PostMapping("/verify-registration-otp")
    public ResponseEntity<?> verifyRegistrationOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        try {

            userService.verifyRegistrationOtp(
                    request.getEmail(),
                    request.getOtp()
            );


            return ResponseEntity.ok(
                    new AuthResponse(
                            "Email verified successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new ErrorResponse(
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        try {

            userService.login(request);


            return ResponseEntity.ok(
                    new AuthResponse(
                            "Login successful"
                    )
            );

        } catch (RuntimeException e) {

            logger.error(
                    "Login failed: {}",
                    e.getMessage()
            );


            return ResponseEntity
                    .badRequest()
                    .body(
                            new ErrorResponse(
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // FORGOT PASSWORD - SEND OTP
    // =====================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody
            ForgotPasswordRequest request) {

        try {

            userService.forgotPassword(request);


            return ResponseEntity.ok(
                    new AuthResponse(
                            "OTP sent to your email"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new ErrorResponse(
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // VERIFY FORGOT PASSWORD OTP
    // =====================================================

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody
            VerifyOtpRequest request) {

        try {

            userService.verifyForgotPasswordOtp(
                    request.getEmail(),
                    request.getOtp()
            );


            return ResponseEntity.ok(
                    new AuthResponse(
                            "OTP verified successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new ErrorResponse(
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // RESET PASSWORD
    // =====================================================

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody
            ResetPasswordRequest request) {

        try {

            String message =
                    userService.resetPassword(request);


            return ResponseEntity.ok(
                    new AuthResponse(message)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            new ErrorResponse(
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // GET USER
    // =====================================================

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(
            @PathVariable String userId) {

        try {

            User user =
                    userService.getUserByUserId(userId);


            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ErrorResponse(
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // VALIDATION ERROR
    // =====================================================

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    handleValidationErrors(
            MethodArgumentNotValidException ex) {


        String message = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->
                        error.getDefaultMessage())
                .orElse("Invalid input");


        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorResponse(message)
                );
    }


    // =====================================================
    // ERROR RESPONSE
    // =====================================================

    public static class ErrorResponse {

        private String message;


        public ErrorResponse(String message) {

            this.message = message;
        }


        public String getMessage() {

            return message;
        }


        public void setMessage(String message) {

            this.message = message;
        }
    }
}