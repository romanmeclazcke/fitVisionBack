package org.example.fitvisionback.payment.repository;

import org.example.fitvisionback.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentHistory, UUID> {
}
