package org.example.fitvisionback.credits.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.fitvisionback.user.entity.User;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credits {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Integer credits = 0;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
