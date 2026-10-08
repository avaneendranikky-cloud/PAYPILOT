package com.paypilot.service;

import com.paypilot.controller.LoginRequest;
import com.paypilot.controller.RegisterRequest;
import com.paypilot.entity.User;
import com.paypilot.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest();

        registerRequest.setUserId("hari123");
        registerRequest.setPanNumber("ABCDE1234F");
        registerRequest.setBankAccountNumber("123456789012");
        registerRequest.setIfscCode("SBIN0001234");
        registerRequest.setBankingPartner("SBI");
        registerRequest.setPassword("Hari@12345");
        registerRequest.setConfirmPassword("Hari@12345");
    }

    // =====================================================
    // TEST 1: Successful Registration
    // =====================================================

    @Test
    void registerUserSuccessfully() {

        when(userRepository.existsByUserId("hari123"))
                .thenReturn(false);

        when(userRepository.existsByPanNumber("ABCDE1234F"))
                .thenReturn(false);

        when(userRepository.existsByBankAccountNumber("123456789012"))
                .thenReturn(false);

        User savedUser = new User();

        savedUser.setUserId("hari123");
        savedUser.setPanNumber("ABCDE1234F");
        savedUser.setBankAccountNumber("123456789012");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);

        assertEquals(
                "hari123",
                result.getUserId()
        );

        verify(userRepository)
                .save(any(User.class));
    }

    // =====================================================
    // TEST 2: Duplicate User ID
    // =====================================================

    @Test
    void registerUserWhenUserAlreadyExists() {

        when(userRepository.existsByUserId("hari123"))
                .thenReturn(true);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.registerUser(registerRequest)
                );

        assertEquals(
                "User ID already exists",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =====================================================
    // TEST 3: Password Mismatch
    // =====================================================

    @Test
    void registerUserWhenPasswordsDoNotMatch() {

        registerRequest.setConfirmPassword("Different@123");

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.registerUser(registerRequest)
                );

        assertEquals(
                "Password and Confirm Password do not match",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =====================================================
    // TEST 4: Successful Login
    // =====================================================

    @Test
    void loginSuccessfully() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        User user = new User();

        user.setUserId("hari123");

        // Encrypt the same password used in LoginRequest
        user.setPassword(
                encoder.encode("Hari@12345")
        );

        user.setFailedAttempts(0);
        user.setAccountLocked(false);

        when(userRepository.findByUserId("hari123"))
                .thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();

        request.setUserId("hari123");
        request.setPassword("Hari@12345");

        User result = userService.login(request);

        assertNotNull(result);

        assertEquals(
                "hari123",
                result.getUserId()
        );

        assertEquals(
                0,
                result.getFailedAttempts()
        );

        assertFalse(
                result.isAccountLocked()
        );

        verify(userRepository)
                .save(user);
    }

    // =====================================================
    // TEST 5: User Not Found
    // =====================================================

    @Test
    void loginWhenUserDoesNotExist() {

        LoginRequest request = new LoginRequest();

        request.setUserId("unknown");
        request.setPassword("Password@123");

        when(userRepository.findByUserId("unknown"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.login(request)
                );

        assertEquals(
                "Invalid User ID or Password",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =====================================================
    // TEST 6: Account Already Locked
    // =====================================================

    @Test
    void loginWhenAccountIsLocked() {

        User user = new User();

        user.setUserId("hari123");
        user.setPassword("anything");
        user.setFailedAttempts(3);
        user.setAccountLocked(true);

        when(userRepository.findByUserId("hari123"))
                .thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();

        request.setUserId("hari123");
        request.setPassword("Hari@12345");

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.login(request)
                );

        assertEquals(
                "Account is locked. Please contact support.",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =====================================================
    // TEST 7: Wrong Password
    // =====================================================

    @Test
    void loginWithWrongPasswordIncreasesAttempts() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        User user = new User();

        user.setUserId("hari123");

        // Correct password is Hari@12345
        user.setPassword(
                encoder.encode("Hari@12345")
        );

        user.setFailedAttempts(0);
        user.setAccountLocked(false);

        when(userRepository.findByUserId("hari123"))
                .thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();

        request.setUserId("hari123");
        request.setPassword("WrongPassword");

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.login(request)
                );

        assertEquals(
                "Invalid User ID or Password. Attempt 1 of 3",
                exception.getMessage()
        );

        assertEquals(
                1,
                user.getFailedAttempts()
        );

        assertFalse(
                user.isAccountLocked()
        );

        verify(userRepository)
                .save(user);
    }

    // =====================================================
    // TEST 8: Account Locks After 3 Failed Attempts
    // =====================================================

    @Test
    void loginLocksAccountAfterThreeFailedAttempts() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        User user = new User();

        user.setUserId("hari123");

        user.setPassword(
                encoder.encode("Hari@12345")
        );

        // Already had 2 failed attempts
        user.setFailedAttempts(2);
        user.setAccountLocked(false);

        when(userRepository.findByUserId("hari123"))
                .thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();

        request.setUserId("hari123");
        request.setPassword("WrongPassword");

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.login(request)
                );

        assertEquals(
                "Account locked after 3 failed login attempts",
                exception.getMessage()
        );

        assertEquals(
                3,
                user.getFailedAttempts()
        );

        assertTrue(
                user.isAccountLocked()
        );

        verify(userRepository)
                .save(user);
    }
}