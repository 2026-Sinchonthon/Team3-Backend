package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.dto.request.CreateTipRequest;
import com.sinchonthon.team3_backend.dto.request.TipCommentRequest;
import com.sinchonthon.team3_backend.dto.request.TipReactionRequest;
import com.sinchonthon.team3_backend.dto.response.TipCommentResponse;
import com.sinchonthon.team3_backend.dto.response.TipDetailResponse;
import com.sinchonthon.team3_backend.dto.response.TipFeedResponse;
import com.sinchonthon.team3_backend.dto.response.TipReactionResponse;
import com.sinchonthon.team3_backend.dto.response.CreateTipResponse;
import com.sinchonthon.team3_backend.service.TipService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tips")
public class TipController {
    private final TipService service;

    public TipController(TipService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<ApiResponse<CreateTipResponse>> create(
            Authentication authentication,
            @Valid @RequestBody CreateTipRequest request) {
        CreateTipResponse response = service.create(currentUserId(authentication), request);
        return ResponseEntity.created(URI.create("/api/tips/" + response.tipId()))
                .body(ApiResponse.success(201, "팁 등록 성공", response));
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

    @GetMapping("/{tipId}")
    ApiResponse<TipDetailResponse> getDetail(@PathVariable Long tipId, Authentication authentication) {
        return ApiResponse.success(200, "게시글 상세 조회 성공", service.getDetail(tipId, currentUserId(authentication)));
    }

    @DeleteMapping("/{tipId}")
    ApiResponse<Void> deleteTip(@PathVariable Long tipId, Authentication authentication) {
        service.deleteTip(tipId, currentUserId(authentication));
        return ApiResponse.success(200, "게시글 삭제 성공", null);
    }

    @PutMapping("/{tipId}/reactions")
    ApiResponse<TipReactionResponse> react(@PathVariable Long tipId, @Valid @RequestBody TipReactionRequest request,
            Authentication authentication) {
        return ApiResponse.success(200, "반응 등록 성공",
                service.react(tipId, currentUserId(authentication), request.isLike()));
    }

    @DeleteMapping("/{tipId}/reactions")
    ApiResponse<TipReactionResponse> cancelReaction(@PathVariable Long tipId, Authentication authentication) {
        return ApiResponse.success(200, "반응 취소 성공", service.cancelReaction(tipId, currentUserId(authentication)));
    }

    @PostMapping("/{tipId}/scraps")
    ApiResponse<Void> scrap(@PathVariable Long tipId, Authentication authentication) {
        service.scrap(tipId, currentUserId(authentication));
        return ApiResponse.success(201, "꿀팁 스크랩 성공", null);
    }

    @DeleteMapping("/{tipId}/scraps")
    ApiResponse<Void> cancelScrap(@PathVariable Long tipId, Authentication authentication) {
        service.cancelScrap(tipId, currentUserId(authentication));
        return ApiResponse.success(200, "꿀팁 스크랩 취소 성공", null);
    }

    @PostMapping("/{tipId}/comments")
    ApiResponse<TipCommentResponse> addComment(@PathVariable Long tipId,
            @Valid @RequestBody TipCommentRequest request, Authentication authentication) {
        return ApiResponse.success(201, "댓글 작성 성공",
                service.addComment(tipId, currentUserId(authentication), request.content()));
    }

    @GetMapping("/{tipId}/comments")
    ApiResponse<Page<TipCommentResponse>> getComments(@PathVariable Long tipId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(200, "댓글 목록 조회 성공", service.getComments(tipId, pageable));
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
