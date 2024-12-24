package org.example.ai_content_creator_hub.dto.auth;

import lombok.Getter;

@Getter
public class RefreshTokenResponse {
    private final String accessToken;

    public RefreshTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
