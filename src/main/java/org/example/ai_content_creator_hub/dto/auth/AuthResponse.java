package org.example.ai_content_creator_hub.dto.auth;

import lombok.Getter;

@Getter
public class AuthResponse {
    private final String accessToken;
    private final String refreshToken;

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}