package org.example.fitvisionback.creditsConsumed.repository;

import org.example.fitvisionback.creditsConsumed.model.CreditsConsumed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CreditsConsumedRepository extends JpaRepository<CreditsConsumed, UUID> {
    List<CreditsConsumed> findAllByUserIdAndConsumedAtBetweenOrderByConsumedAt(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
}
