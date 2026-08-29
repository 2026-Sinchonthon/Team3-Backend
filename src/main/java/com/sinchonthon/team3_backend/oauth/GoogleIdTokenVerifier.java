package com.sinchonthon.team3_backend.oauth;

public interface GoogleIdTokenVerifier {
    GoogleUserInfo verify(String token);

    record GoogleUserInfo(String sub, String email, boolean emailVerified, String name, String picture) {}
}
