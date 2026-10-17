
package com.ceos.plabfootball.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String TOKEN_TYPE = "type";
    private static final String ACCESS = "ACCESS";
    private static final String REFRESH = "REFRESH";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        // secretKey 생성
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        if (accessTokenExpiration <= 0 || refreshTokenExpiration <= 0) {
            throw new IllegalArgumentException("JWT 만료 시간은 0보다 커야 합니다.");
        }

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Access Token 발급
    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS, accessTokenExpiration);
    }

    // Refresh Token 발급
    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH, refreshTokenExpiration);
    }

    // 공통 JWT 생성
    private String createToken(Long userId, String type, long expiration) {

        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(TOKEN_TYPE, type)
                .issuedAt(now)
                .expiration(expiredAt)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // JWT 서명 및 만료 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT에서 사용자 ID 추출
    public Long getUserId(String token) {
        String subject = getClaims(token).getSubject();

        if (subject == null) {
            throw new JwtException("JWT 사용자 정보가 없습니다.");
        }

        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            throw new JwtException("JWT 사용자 ID 형식이 올바르지 않습니다.", e);
        }
    }

    // Access Token 여부 확인
    public boolean isAccessToken(String token) {
        return hasTokenType(token, ACCESS);
    }

    // Refresh Token 여부 확인
    public boolean isRefreshToken(String token) {
        return hasTokenType(token, REFRESH);
    }

    private boolean hasTokenType(String token, String type) {
        try {
            return type.equals(getClaims(token).get(TOKEN_TYPE, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 서명을 검증한 뒤 Claims 반환
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
