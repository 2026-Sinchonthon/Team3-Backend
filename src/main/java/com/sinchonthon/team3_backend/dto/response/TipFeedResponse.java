package com.sinchonthon.team3_backend.dto.response;

import java.time.Instant;

public record TipFeedResponse(
        Long tipId,
        String title,
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
        Long likeCount
) {}
