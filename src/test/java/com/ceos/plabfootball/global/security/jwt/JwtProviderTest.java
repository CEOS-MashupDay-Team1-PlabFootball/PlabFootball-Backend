
package com.ceos.plabfootball.global.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String testSecret =
                "test-secret-key-for-jwt-unit-testing-123456789";

        jwtProvider = new JwtProvider(
                testSecret,
                3600000L,
                604800000L
        );
    }

    @Test
    void accessToken_생성_및_검증() {
        // given
        Long userId = 1L;

        // when
        String accessToken = jwtProvider.createAccessToken(userId);

        // then
        assertTrue(jwtProvider.validateToken(accessToken));
        assertTrue(jwtProvider.isAccessToken(accessToken));
        assertFalse(jwtProvider.isRefreshToken(accessToken));
        assertEquals(userId, jwtProvider.getUserId(accessToken));
    }

    @Test
    void refreshToken_생성_및_검증() {
        // given
        Long userId = 1L;

        // when
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // then
        assertTrue(jwtProvider.validateToken(refreshToken));
        assertTrue(jwtProvider.isRefreshToken(refreshToken));
        assertFalse(jwtProvider.isAccessToken(refreshToken));
        assertEquals(userId, jwtProvider.getUserId(refreshToken));
    }

    @Test
    void 잘못된_토큰_검증_실패() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when & then
        assertFalse(jwtProvider.validateToken(invalidToken));
    }

    @Test
    void 만료된_토큰_검증_실패() {
        // given
        JwtProvider shortLivedProvider = new JwtProvider(
                "test-secret-key-for-jwt-unit-testing-123456789",
                1L,
                604800000L
        );

        // when
        String token = shortLivedProvider.createAccessToken(1L);

        // then
        assertFalse(jwtProvider.validateToken(token));
    }
}
