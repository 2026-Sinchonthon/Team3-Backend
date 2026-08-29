package com.sinchonthon.team3_backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private static final String SOCIAL_PASSWORD = "{SOCIAL_GOOGLE}";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(nullable = false, length = 255)
    private String passwordHash;
    @Column(nullable = false, unique = true, length = 30)
    private String nickname;
    @Column(nullable = false)
    private int trustScore;
    @Column(nullable = false)
    private int livingAloneYears;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public User(String email) {
        this.email = email;
        this.passwordHash = SOCIAL_PASSWORD;
        this.nickname = "user-" + UUID.randomUUID().toString().substring(0, 8);
        this.trustScore = 50;
        this.livingAloneYears = 0;
    }

    @PrePersist
    void createTimestamps() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}
