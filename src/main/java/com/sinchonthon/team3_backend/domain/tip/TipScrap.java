package com.sinchonthon.team3_backend.domain.tip;

import com.sinchonthon.team3_backend.domain.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "tip_scraps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TipScrap {
    @EmbeddedId
    private TipScrapId id;

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

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public TipScrap(User user, Tip tip) {
        this.user = user;
        this.tip = tip;
        this.id = new TipScrapId(user.getId(), tip.getId());
    }

    @PrePersist
    void createTimestamp() {
        this.createdAt = Instant.now();
    }
}
