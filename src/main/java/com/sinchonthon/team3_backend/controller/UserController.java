package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.dto.request.OnboardingRequest;
import com.sinchonthon.team3_backend.dto.response.OnboardingResponse;
import com.sinchonthon.team3_backend.dto.response.MyTipListResponse;
import com.sinchonthon.team3_backend.service.TipService;
import com.sinchonthon.team3_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService service;
    private final TipService tipService;

    public UserController(UserService service, TipService tipService) {
        this.service = service;
        this.tipService = tipService;
    }

    @PatchMapping("/me/onboarding")
    ApiResponse<OnboardingResponse> updateOnboarding(
            Authentication authentication,
            @Valid @RequestBody OnboardingRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        return ApiResponse.success(200, "온보딩 정보 등록 성공",
                service.updateOnboarding(userId, request));
    }

    @GetMapping("/me/tips")
    ApiResponse<MyTipListResponse> getMyTips(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = Long.valueOf(authentication.getName());
        return ApiResponse.success(200, "내가 작성한 팁 조회 성공",
                tipService.findMine(userId, page, size));
    }
}
