package com.sinchonthon.team3_backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "tip_reactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TipReaction {
    @EmbeddedId
    private TipReactionId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @MapsId("tipId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tip_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tip tip;

    @Column(nullable = false)
    private boolean isLike;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    public TipReaction(User user, Tip tip, boolean isLike) {
        this.user = user;
        this.tip = tip;
        this.id = new TipReactionId(user.getId(), tip.getId());
        this.isLike = isLike;
    }

    public void changeReaction(boolean isLike) {
        this.isLike = isLike;
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
