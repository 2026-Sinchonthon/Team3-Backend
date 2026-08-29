package com.sinchonthon.team3_backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record TipReactionRequest(@NotNull Boolean isLike) {}
