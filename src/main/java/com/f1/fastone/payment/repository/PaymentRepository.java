package com.f1.fastone.payment.repository;

import com.f1.fastone.payment.entity.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPaymentKey(String paymentKey);
    boolean existsByPaymentKey(String paymentKey);
}