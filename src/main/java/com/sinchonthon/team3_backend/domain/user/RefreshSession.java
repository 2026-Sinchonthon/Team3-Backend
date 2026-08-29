package com.sinchonthon.team3_backend.domain.user;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshSession {
    @Id private String id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false, unique = true, length = 64) private String tokenHash;
    @Column(nullable = false) private Instant expiresAt;
    private Instant revokedAt;
    private String deviceInfo;
    @Column(nullable = false) private Instant lastUsedAt;

    public RefreshSession(String id, Long userId, String hash, Instant expiresAt, String device) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = hash;
        this.expiresAt = expiresAt;
        this.deviceInfo = device;
        this.lastUsedAt = Instant.now();
    }

    public boolean usable() { return revokedAt == null && expiresAt.isAfter(Instant.now()); }
    public void revoke() { revokedAt = Instant.now(); lastUsedAt = revokedAt; }
}
