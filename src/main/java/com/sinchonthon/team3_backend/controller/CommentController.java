package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.service.TipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comments", description = "댓글 삭제")
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final TipService service;

    public CommentController(TipService service) {
        this.service = service;
    }

    @Operation(summary = "댓글 삭제", description = "본인 댓글만 삭제할 수 있습니다.")
    @DeleteMapping("/{commentId}")
    ApiResponse<Void> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        service.deleteComment(commentId, Long.valueOf(authentication.getName()));
        return ApiResponse.success(200, "댓글 삭제 성공", null);
    }
}
