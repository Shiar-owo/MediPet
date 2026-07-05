package com.medipet.medipet.user.domain.port.outbound;

public interface TokenProvider {

    String generateAccessToken(String email, String role);

    String generateRefreshToken(String email);

    String extractEmail(String token);

    boolean validateToken(String token);

    String extractRole(String token);
}
