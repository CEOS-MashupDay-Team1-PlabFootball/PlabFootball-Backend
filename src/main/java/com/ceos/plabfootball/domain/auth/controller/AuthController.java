
package com.ceos.plabfootball.domain.auth.controller;

import com.ceos.plabfootball.domain.auth.dto.GuestLoginResponse;
import com.ceos.plabfootball.domain.auth.dto.TokenPair;
import com.ceos.plabfootball.domain.auth.service.AuthService;
import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import com.ceos.plabfootball.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<GuestLoginResponse>> guestLogin() {

        // 게스트 생성 및 AT/RT 발급
        TokenPair tokens = authService.guestLogin();

        // Refresh Token을 HttpOnly 쿠키로 설정
        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(false) // 로컬 HTTP 개발용
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        // Access Token은 응답 Body로 전달
        GuestLoginResponse response = GuestLoginResponse.of(
                tokens.userId(),
                tokens.accessToken()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.onSuccess(response));
    }

    // 기존 게스트 확인 및 AT/RT 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<GuestLoginResponse>> refresh(
            @CookieValue(value = "refreshToken", required = false)
            String refreshToken
    ) {

        // Refresh Token이 없으면 401 반환
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new GeneralException(
                    ErrorStatus.AUTH_INVALID_REFRESH_TOKEN
            );
        }

        // 기존 게스트 확인 및 새 AT/RT 발급
        TokenPair tokens = authService.refresh(refreshToken);

        // 새로운 Refresh Token을 쿠키에 설정
        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(false) // 로컬 개발용, 운영 HTTPS에서는 true
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        // 새로운 Access Token 응답 데이터 생성
        GuestLoginResponse response = GuestLoginResponse.of(
                tokens.userId(),
                tokens.accessToken()
        );

        // 공통 응답 형식으로 반환
        // 갱신된 RT를 브라우저에 다시 저장하도록 전달
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.onSuccess(response));
    }

}
