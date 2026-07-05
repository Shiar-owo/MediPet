package com.medipet.medipet.user.domain.port.inbound;

public interface    AuthService {

    LoginResult login(String email, String password);

    LoginResult refreshToken(String refreshToken);

    void logout(String refreshToken);

    record LoginResult(String accessToken, String refreshToken) {
    }
}
