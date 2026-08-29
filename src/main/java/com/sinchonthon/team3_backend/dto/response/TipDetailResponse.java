package com.sinchonthon.team3_backend.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public record TipDetailResponse(
        Long tipId,
        String title,
        String content,
        LocalDate visitedAt,
        Instant validUntil,
        Long categoryId,
        String categoryName,
        Long placeId,
        String placeName,
        Long writerId,
        String writerNickname,
        int writerTrustScore,
        int writerLivingAloneYears,
        boolean isFiltered,
        Instant createdAt,
        Instant updatedAt,
        Long likeCount,
        Long dislikeCount,
        Boolean myReaction
) {}
