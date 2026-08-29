package com.sinchonthon.team3_backend.dto.response;

import java.math.BigDecimal;

public record PlaceMapResponse(
        Long id,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        String categoryName
) {}
