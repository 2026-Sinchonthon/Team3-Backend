package com.sinchonthon.team3_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateTipRequest(
        @NotNull(message = "카테고리는 필수입니다.")
        @Positive(message = "카테고리 ID는 양수여야 합니다.")
        Long categoryId,

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 5000, message = "내용은 5000자 이하여야 합니다.")
        String content,

        @NotNull(message = "위치는 필수입니다.")
        @Valid
        Location location) {

    public record Location(
            @NotBlank(message = "장소명은 필수입니다.")
            @Size(max = 150, message = "장소명은 150자 이하여야 합니다.")
            String name,

            @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
            String address,

            @NotNull(message = "위도는 필수입니다.")
            @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
            @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
            @Digits(integer = 3, fraction = 7, message = "위도는 소수점 아래 7자리 이하여야 합니다.")
            BigDecimal latitude,

            @NotNull(message = "경도는 필수입니다.")
            @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
            @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
            @Digits(integer = 3, fraction = 7, message = "경도는 소수점 아래 7자리 이하여야 합니다.")
            BigDecimal longitude) {
    }
}
