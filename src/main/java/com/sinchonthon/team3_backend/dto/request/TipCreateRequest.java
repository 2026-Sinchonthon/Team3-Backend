package com.sinchonthon.team3_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TipCreateRequest(
        @NotNull Long categoryId,
        @NotBlank String kakaoPlaceId,
        @NotBlank String placeName,
        String placeAddress,
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude,
        @NotBlank String title,
        @NotBlank String content
) {}