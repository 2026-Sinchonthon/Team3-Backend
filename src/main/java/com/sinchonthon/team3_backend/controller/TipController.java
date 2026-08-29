package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.dto.response.TipFeedResponse;
import com.sinchonthon.team3_backend.service.TipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tips")
public class TipController {
    private final TipService service;

    public TipController(TipService service) {
        this.service = service;
    }

    @GetMapping
    ApiResponse<Page<TipFeedResponse>> getFeed(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(200, "게시글 피드 조회 성공", service.getFeed(categoryId, userId, keyword, sort, pageable));
    }
}
