package com.cafe.mobile.shcafe.payment.repository;

import com.cafe.mobile.shcafe.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findTopByOrderOrderIdOrderByPaymentIdDesc(Long orderId);

}
