package com.arnavgpt.valoride.payment.repository;

import com.arnavgpt.valoride.payment.entity.Payment;
import com.arnavgpt.valoride.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByRideId(UUID rideId);

    Optional<Payment> findByStripePaymentIntentId(String paymentIntentId);

    Page<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Payment> findByUserIdAndStatus(UUID userId, PaymentStatus status, Pageable pageable);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByRideId(UUID rideId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user.id = :userId AND p.status = 'COMPLETED'")
    BigDecimal getTotalPaidByUser(@Param("userId") UUID userId);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime")
    List<Payment> findStalePendingPayments(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueInDateRange(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);
}