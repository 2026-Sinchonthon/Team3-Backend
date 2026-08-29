package com.sinchonthon.team3_backend.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "user_follows")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFollow {
    @EmbeddedId
    private UserFollowId id;

    @MapsId("followerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User follower;

    @MapsId("followingId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User following;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public UserFollow(User follower, User following) {
        this.follower = follower;
        this.following = following;
        this.id = new UserFollowId(follower.getId(), following.getId());
    }

    @PrePersist
    void createTimestamp() {
        this.createdAt = Instant.now();
    }
}
