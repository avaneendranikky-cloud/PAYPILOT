package com.example.paypilotapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.paypilotapp.entity.Bill;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByUserId(Long userId);

    List<Bill> findByUserIdAndBillCategory(
            Long userId,
            String billCategory
    );

    List<Bill> findByUserIdAndBillNameContainingIgnoreCase(
            Long userId,
            String billName
    );

    @Query("""
           SELECT b
           FROM Bill b
           WHERE b.userId = :userId
           AND b.dueDate >= :today
           ORDER BY b.dueDate ASC
           """)
    List<Bill> findUpcomingBills(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );

    @Query("""
           SELECT b
           FROM Bill b
           WHERE b.userId = :userId
           AND b.dueDate < :today
           AND b.status <> 'PAID'
           ORDER BY b.dueDate ASC
           """)
    List<Bill> findOverdueBills(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );
}