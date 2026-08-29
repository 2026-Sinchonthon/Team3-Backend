package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.dto.request.CreateTipRequest;
import com.sinchonthon.team3_backend.dto.response.CreateTipResponse;
import com.sinchonthon.team3_backend.service.TipService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tips")
public class TipController {
    private final TipService service;

    public TipController(TipService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<ApiResponse<CreateTipResponse>> create(
            Authentication authentication,
            @Valid @RequestBody CreateTipRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        CreateTipResponse response = service.create(userId, request);
        return ResponseEntity.created(URI.create("/api/v1/tips/" + response.tipId()))
                .body(ApiResponse.success(201, "팁 등록 성공", response));
    }
}
