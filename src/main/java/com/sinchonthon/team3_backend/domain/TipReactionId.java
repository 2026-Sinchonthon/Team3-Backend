package com.sinchonthon.team3_backend.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TipReactionId implements Serializable {
    private Long userId;
    private Long tipId;

    public TipReactionId(Long userId, Long tipId) {
        this.userId = userId;
        this.tipId = tipId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipReactionId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(tipId, that.tipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tipId);
    }
}
