
package com.ceos.plabfootball.domain.auth.controller;

import com.ceos.plabfootball.domain.auth.dto.GuestLoginResponse;
import com.ceos.plabfootball.domain.auth.dto.TokenPair;
import com.ceos.plabfootball.domain.auth.service.AuthService;
import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
}
