package com.ceos.plabfootball.domain.auth.dto;

public record TokenPair(
        Long userId,
        String accessToken,
        String refreshToken
) {
}