package com.sinchonthon.team3_backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTipResponse(
        Long tipId,
        Long userId,
        CategoryInfo category,
        String title,
        String content,
        PlaceInfo location,
        Instant validUntil,
        Instant createdAt) {

    public record CategoryInfo(Long id, String name) {
    }

    public record PlaceInfo(
            Long placeId,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude) {
    }
}
