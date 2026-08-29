package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.dto.request.TipCommentRequest;
import com.sinchonthon.team3_backend.dto.request.TipReactionRequest;
import com.sinchonthon.team3_backend.dto.response.TipCommentResponse;
import com.sinchonthon.team3_backend.dto.response.TipDetailResponse;
import com.sinchonthon.team3_backend.dto.response.TipFeedResponse;
import com.sinchonthon.team3_backend.dto.response.TipReactionResponse;
import com.sinchonthon.team3_backend.service.TipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tips", description = "게시글(꿀팁) 피드/상세/삭제, 추천·비추, 스크랩, 댓글")
@RestController
@RequestMapping("/api/tips")
public class TipController {
    private final TipService service;

    public TipController(TipService service) {
        this.service = service;
    }

    @Operation(summary = "게시글 피드 조회",
            description = "카테고리/작성자/키워드로 필터링하고, latest(최신순)/likes(추천순)/trust(신뢰도순)/relevance(관련도순)로 정렬합니다. "
                    + "키워드 검색은 띄어쓰기 차이를 무시합니다.")
    @GetMapping
    ApiResponse<Page<TipFeedResponse>> getFeed(
            @Parameter(description = "카테고리 ID로 필터링") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "작성자 ID로 필터링 (유저별 필터링)") @RequestParam(required = false) Long userId,
            @Parameter(description = "제목/본문 키워드 검색") @RequestParam(required = false) String keyword,
            @Parameter(description = "정렬 기준: latest, likes, trust, relevance") @RequestParam(defaultValue = "latest") String sort,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(200, "게시글 피드 조회 성공", service.getFeed(categoryId, userId, keyword, sort, pageable));
    }

    @Operation(summary = "게시글 상세 조회", description = "작성자 신뢰지수/자취연차, 추천·비추 카운트, 내 반응 여부를 함께 반환합니다.")
    @GetMapping("/{tipId}")
    ApiResponse<TipDetailResponse> getDetail(@PathVariable Long tipId, Authentication authentication) {
        return ApiResponse.success(200, "게시글 상세 조회 성공", service.getDetail(tipId, currentUserId(authentication)));
    }

    @Operation(summary = "게시글 삭제", description = "본인 게시글만 삭제할 수 있습니다. 연관된 반응/스크랩/댓글도 함께 삭제됩니다.")
    @DeleteMapping("/{tipId}")
    ApiResponse<Void> deleteTip(@PathVariable Long tipId, Authentication authentication) {
        service.deleteTip(tipId, currentUserId(authentication));
        return ApiResponse.success(200, "게시글 삭제 성공", null);
    }

    @Operation(summary = "추천/비추 등록·변경", description = "이미 반응이 있으면 값을 변경합니다(upsert).")
    @PutMapping("/{tipId}/reactions")
    ApiResponse<TipReactionResponse> react(@PathVariable Long tipId, @Valid @RequestBody TipReactionRequest request,
            Authentication authentication) {
        return ApiResponse.success(200, "반응 등록 성공",
                service.react(tipId, currentUserId(authentication), request.isLike()));
    }

    @Operation(summary = "추천/비추 취소")
    @DeleteMapping("/{tipId}/reactions")
    ApiResponse<TipReactionResponse> cancelReaction(@PathVariable Long tipId, Authentication authentication) {
        return ApiResponse.success(200, "반응 취소 성공", service.cancelReaction(tipId, currentUserId(authentication)));
    }

    @Operation(summary = "꿀팁 스크랩", description = "이미 스크랩한 경우 예외 없이 무시됩니다.")
    @PostMapping("/{tipId}/scraps")
    ApiResponse<Void> scrap(@PathVariable Long tipId, Authentication authentication) {
        service.scrap(tipId, currentUserId(authentication));
        return ApiResponse.success(201, "꿀팁 스크랩 성공", null);
    }

    @Operation(summary = "꿀팁 스크랩 취소")
    @DeleteMapping("/{tipId}/scraps")
    ApiResponse<Void> cancelScrap(@PathVariable Long tipId, Authentication authentication) {
        service.cancelScrap(tipId, currentUserId(authentication));
        return ApiResponse.success(200, "꿀팁 스크랩 취소 성공", null);
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/{tipId}/comments")
    ApiResponse<TipCommentResponse> addComment(@PathVariable Long tipId,
            @Valid @RequestBody TipCommentRequest request, Authentication authentication) {
        return ApiResponse.success(201, "댓글 작성 성공",
                service.addComment(tipId, currentUserId(authentication), request.content()));
    }

    @Operation(summary = "댓글 목록 조회", description = "작성자 신뢰지수/자취연차를 포함합니다.")
    @GetMapping("/{tipId}/comments")
    ApiResponse<Page<TipCommentResponse>> getComments(@PathVariable Long tipId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(200, "댓글 목록 조회 성공", service.getComments(tipId, pageable));
    }

    private Long currentUserId(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}
