package com.sinchonthon.team3_backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TipCommentRequest(@NotBlank String content) {}
