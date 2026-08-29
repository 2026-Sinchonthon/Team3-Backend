package com.sinchonthon.team3_backend.service;

import com.sinchonthon.team3_backend.domain.user.RefreshSession;
import com.sinchonthon.team3_backend.domain.user.User;
import com.sinchonthon.team3_backend.exception.ApiException;
import com.sinchonthon.team3_backend.oauth.GoogleIdTokenVerifier;
import com.sinchonthon.team3_backend.repository.RefreshSessionRepository;
import com.sinchonthon.team3_backend.repository.UserRepository;
import com.sinchonthon.team3_backend.security.JwtService;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final RefreshSessionRepository sessions;
    private final GoogleIdTokenVerifier verifier;
    private final JwtService jwt;

    public AuthService(UserRepository users, RefreshSessionRepository sessions,
                       GoogleIdTokenVerifier verifier, JwtService jwt) {
        this.users = users;
        this.sessions = sessions;
        this.verifier = verifier;
        this.jwt = jwt;
    }

    @Transactional
    public Login login(String token, String device) {
        var googleUser = verifier.verify(token);
        var found = users.findByEmail(googleUser.email());
        boolean fresh = found.isEmpty();
        User user = found.orElseGet(() -> users.save(new User(googleUser.email())));
        return new Login(user, issue(user, device), fresh);
    }

    @Transactional
    public Tokens refresh(String raw, String device) {
        if (raw == null) throw unauthorized("Refresh Token이 필요합니다.");
        Claims claims;
        try {
            claims = jwt.parse(raw);
        } catch (Exception e) {
            throw unauthorized("Refresh Token이 유효하지 않습니다.");
        }
        if (!"refresh".equals(claims.get("type"))) {
            throw unauthorized("Refresh Token이 유효하지 않습니다.");
        }
        RefreshSession old = sessions.findByTokenHash(jwt.hash(raw))
                .filter(RefreshSession::usable)
                .orElseThrow(() -> unauthorized("Refresh Token이 유효하지 않습니다."));
        old.revoke();
        User user = users.findById(old.getUserId())
                .orElseThrow(() -> unauthorized("사용자를 찾을 수 없습니다."));
        return issue(user, device);
    }

    @Transactional
    public void logout(String raw) {
        if (raw != null) sessions.findByTokenHash(jwt.hash(raw)).ifPresent(RefreshSession::revoke);
    }

    private Tokens issue(User user, String device) {
        String sessionId = UUID.randomUUID().toString();
        String refresh = jwt.refresh(sessionId, user.getId());
        sessions.save(new RefreshSession(sessionId, user.getId(), jwt.hash(refresh),
                Instant.now().plusSeconds(jwt.refreshSeconds()), device));
        return new Tokens(jwt.access(user.getId()),
                refresh, jwt.accessSeconds(), jwt.refreshSeconds());
    }

    private ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message);
    }

    public record Tokens(String access, String refresh, long accessSeconds, long refreshSeconds) {}
    public record Login(User user, Tokens tokens, boolean fresh) {}
}
