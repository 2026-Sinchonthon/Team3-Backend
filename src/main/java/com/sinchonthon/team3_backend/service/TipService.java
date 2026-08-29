package com.sinchonthon.team3_backend.service;

import com.sinchonthon.team3_backend.domain.tip.Tip;
import com.sinchonthon.team3_backend.domain.tip.TipReaction;
import com.sinchonthon.team3_backend.domain.tip.TipReactionId;
import com.sinchonthon.team3_backend.dto.response.TipDetailResponse;
import com.sinchonthon.team3_backend.dto.response.TipFeedResponse;
import com.sinchonthon.team3_backend.dto.response.TipReactionResponse;
import com.sinchonthon.team3_backend.exception.ApiException;
import com.sinchonthon.team3_backend.repository.TipReactionRepository;
import com.sinchonthon.team3_backend.repository.TipRepository;
import com.sinchonthon.team3_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipService {
    private static final long MIN_REACTION_THRESHOLD = 5;
    private static final double DISLIKE_RATIO_THRESHOLD = 0.5;

    private final TipRepository tips;
    private final TipReactionRepository reactions;
    private final UserRepository users;

    public TipService(TipRepository tips, TipReactionRepository reactions, UserRepository users) {
        this.tips = tips;
        this.reactions = reactions;
        this.users = users;
    }

    public Page<TipFeedResponse> getFeed(Long categoryId, Long userId, String keyword, String sort, Pageable pageable) {
        return "likes".equalsIgnoreCase(sort)
                ? tips.findFeedByLikes(categoryId, userId, keyword, pageable)
                : tips.findFeedByLatest(categoryId, userId, keyword, pageable);
    }

    public TipDetailResponse getDetail(Long tipId, Long currentUserId) {
        return tips.findDetailById(tipId, currentUserId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."));
    }

    @Transactional
    public TipReactionResponse react(Long tipId, Long userId, boolean isLike) {
        Tip tip = tips.findById(tipId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."));
        TipReactionId id = new TipReactionId(userId, tipId);
        reactions.findById(id)
                .map(existing -> { existing.changeReaction(isLike); return existing; })
                .orElseGet(() -> reactions.save(new TipReaction(users.getReferenceById(userId), tip, isLike)));
        refreshFilteredState(tip);
        return summarize(tip, userId);
    }

    @Transactional
    public TipReactionResponse cancelReaction(Long tipId, Long userId) {
        Tip tip = tips.findById(tipId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."));
        reactions.deleteById(new TipReactionId(userId, tipId));
        refreshFilteredState(tip);
        return summarize(tip, userId);
    }

    private void refreshFilteredState(Tip tip) {
        long total = reactions.countByTip(tip);
        if (total < MIN_REACTION_THRESHOLD) {
            tip.updateFiltered(false);
            return;
        }
        long dislikes = reactions.countByTipAndIsLikeFalse(tip);
        tip.updateFiltered((double) dislikes / total >= DISLIKE_RATIO_THRESHOLD);
    }

    private TipReactionResponse summarize(Tip tip, Long userId) {
        long likeCount = reactions.countByTipAndIsLikeTrue(tip);
        long dislikeCount = reactions.countByTipAndIsLikeFalse(tip);
        Boolean myReaction = reactions.findById(new TipReactionId(userId, tip.getId()))
                .map(TipReaction::isLike).orElse(null);
        return new TipReactionResponse(likeCount, dislikeCount, myReaction, tip.isFiltered());
    }
}
