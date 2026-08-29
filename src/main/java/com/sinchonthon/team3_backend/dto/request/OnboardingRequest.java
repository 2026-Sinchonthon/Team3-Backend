package com.sinchonthon.team3_backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OnboardingRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다.")
        String nickname,
        @Min(value = 0, message = "자취 연차는 0 이상이어야 합니다.")
        @Max(value = 100, message = "자취 연차는 100 이하여야 합니다.")
        int livingAloneYears) {
}
