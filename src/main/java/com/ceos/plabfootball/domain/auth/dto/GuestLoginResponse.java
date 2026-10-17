package com.ceos.plabfootball.domain.auth.dto;

public record GuestLoginResponse(
        Long userId,
        String accessToken,
        String tokenType
) {
    public static GuestLoginResponse of(
            Long userId,
            String accessToken
    ) {
        return new GuestLoginResponse(
                userId,
                accessToken,
                "Bearer"
        );
    }
}