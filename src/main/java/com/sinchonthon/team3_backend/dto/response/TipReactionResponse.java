package com.sinchonthon.team3_backend.dto.response;

public record TipReactionResponse(long likeCount, long dislikeCount, Boolean myReaction, boolean isFiltered) {}
