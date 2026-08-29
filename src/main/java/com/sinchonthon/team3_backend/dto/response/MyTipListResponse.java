package com.sinchonthon.team3_backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record MyTipListResponse(
        List<TipItem> tips,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext) {

    public record TipItem(
            Long tipId,
            CategoryInfo category,
            String title,
            String content,
            PlaceInfo location,
            Status status,
            Instant validUntil,
            Instant createdAt,
            Instant updatedAt) {
    }

    public record CategoryInfo(Long id, String name) {
    }

    public record PlaceInfo(
            Long placeId,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude) {
    }

    public enum Status {
        ACTIVE,
        EXPIRED
    }
}
