package com.paypilot.repository;

import com.paypilot.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    boolean existsByPanNumber(String panNumber);

    boolean existsByBankAccountNumber(String bankAccountNumber);
}