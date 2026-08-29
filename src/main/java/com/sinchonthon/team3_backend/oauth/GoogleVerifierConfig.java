package com.sinchonthon.team3_backend.oauth;

import com.sinchonthon.team3_backend.exception.ApiException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidationException;

@Configuration
public class GoogleVerifierConfig {
    @Bean
    GoogleIdTokenVerifier googleVerifier(
            @Value("${app.google.issuer}") String issuer,
            @Value("${app.google.client-id}") String clientId) {
        AtomicReference<JwtDecoder> decoder = new AtomicReference<>();
        return token -> {
            try {
                JwtDecoder activeDecoder = decoder.get();
                if (activeDecoder == null) {
                    activeDecoder = JwtDecoders.fromIssuerLocation(issuer);
                    decoder.compareAndSet(null, activeDecoder);
                }
                Jwt jwt = decoder.get().decode(token);
                if (!jwt.getAudience().contains(clientId)) {
                    throw new JwtValidationException("Invalid audience", List.of());
                }
                String email = jwt.getClaimAsString("email");
                boolean emailVerified = Boolean.TRUE.equals(jwt.getClaim("email_verified"));
                if (!emailVerified || email == null || email.isBlank()) {
                    throw new JwtValidationException("Google email is not verified", List.of());
                }
                return new GoogleIdTokenVerifier.GoogleUserInfo(
                        jwt.getSubject(), email, emailVerified,
                        jwt.getClaimAsString("name"), jwt.getClaimAsString("picture"));
            } catch (Exception e) {
                throw invalidToken();
            }
        };
    }

    private ApiException invalidToken() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Google ID Token을 확인할 수 없습니다.");
    }
}
