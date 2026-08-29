package com.sinchonthon.team3_backend.controller;

import com.sinchonthon.team3_backend.common.ApiResponse;
import com.sinchonthon.team3_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;
    private final boolean secureCookie;

    public AuthController(AuthService service,
                          @Value("${app.auth.refresh-cookie-secure:true}") boolean secureCookie) {
        this.service = service;
        this.secureCookie = secureCookie;
    }

    @PostMapping("/oauth/google")
    ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest, HttpServletResponse response) {
        var login = service.login(request.idToken(), servletRequest.getHeader("User-Agent"));
        setRefreshCookie(response, login.tokens().refresh(), login.tokens().refreshSeconds());
        int code = login.fresh() ? 201 : 200;
        return ResponseEntity.status(code).body(ApiResponse.success(code, "Google 로그인 성공",
                new LoginResponse(login.user().getId(), login.tokens().access(),
                        login.tokens().accessSeconds(), login.fresh())));
    }

    @PostMapping("/token/refresh")
    ApiResponse<TokenResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String raw,
            HttpServletRequest request, HttpServletResponse response) {
        var tokens = service.refresh(raw, request.getHeader("User-Agent"));
        setRefreshCookie(response, tokens.refresh(), tokens.refreshSeconds());
        return ApiResponse.success(200, "토큰 재발급 성공",
                new TokenResponse(tokens.access(), tokens.accessSeconds()));
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String raw,
            HttpServletResponse response) {
        service.logout(raw);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie("", 0).toString());
        return ApiResponse.success(200, "로그아웃 성공", null);
    }

    private void setRefreshCookie(HttpServletResponse response, String token, long seconds) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(token, seconds).toString());
    }

    private ResponseCookie cookie(String token, long seconds) {
        return ResponseCookie.from("refresh_token", token).httpOnly(true).secure(secureCookie)
                .sameSite("Lax").path("/api/v1/auth").maxAge(Duration.ofSeconds(seconds)).build();
    }

    public record LoginRequest(@NotBlank String idToken) {}
    public record LoginResponse(Long userId, String accessToken, long accessTokenExpiresIn,
            boolean isNewUser) {}
    public record TokenResponse(String accessToken, long accessTokenExpiresIn) {}
}
