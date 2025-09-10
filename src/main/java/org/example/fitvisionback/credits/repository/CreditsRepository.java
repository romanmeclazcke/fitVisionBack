package org.example.fitvisionback.credits.repository;

import org.example.fitvisionback.credits.model.Credits;
import org.example.fitvisionback.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CreditsRepository extends JpaRepository<Credits, UUID> {


    Credits findByUser(User user);
}
