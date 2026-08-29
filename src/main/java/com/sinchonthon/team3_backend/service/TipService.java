package com.sinchonthon.team3_backend.service;

import com.sinchonthon.team3_backend.dto.response.TipFeedResponse;
import com.sinchonthon.team3_backend.repository.TipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TipService {
    private final TipRepository tips;

    public TipService(TipRepository tips) {
        this.tips = tips;
    }

    public Page<TipFeedResponse> getFeed(Long categoryId, Long userId, String keyword, String sort, Pageable pageable) {
        return "likes".equalsIgnoreCase(sort)
                ? tips.findFeedByLikes(categoryId, userId, keyword, pageable)
                : tips.findFeedByLatest(categoryId, userId, keyword, pageable);
    }
}
