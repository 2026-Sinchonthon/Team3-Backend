package com.sinchonthon.team3_backend.dto.response;

import java.time.Instant;

public record TipCommentResponse(
        Long commentId,
        Long tipId,
        Long writerId,
        String writerNickname,
        int writerTrustScore,
        int writerLivingAloneYears,
        String content,
        Instant createdAt,
        Instant updatedAt
) {}
