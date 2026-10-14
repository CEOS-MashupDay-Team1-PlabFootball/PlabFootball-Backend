
package com.ceos.plabfootball.global.security.jwt;

import com.ceos.plabfootball.domain.user.entity.User;
import com.ceos.plabfootball.domain.user.repository.UserRepository;

import com.ceos.plabfootball.global.security.handler.AuthErrorHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthErrorHandler authErrorHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Bearer 토큰이 없으면 다음 필터로 전달
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        // JWT 유효성 및 Access Token 타입 검증
        if (!jwtProvider.validateToken(token)
                || !jwtProvider.isAccessToken(token)) {

            authErrorHandler.sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "AUTH_INVALID_TOKEN",
                    "유효하지 않은 Access Token입니다."
            );
            return;
        }

        Long userId;

        try {
            userId = jwtProvider.getUserId(token);
        } catch (RuntimeException e) {
            authErrorHandler.sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "AUTH_INVALID_TOKEN",
                    "JWT 사용자 정보가 올바르지 않습니다."
            );
            return;
        }

        User user = userRepository.findById(userId).orElse(null);

        // 사용자가 없거나 게스트 보관 기간이 만료된 경우
        if (user == null || user.isExpired()) {
            authErrorHandler.sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "AUTH_INVALID_GUEST",
                    "유효하지 않은 게스트 사용자입니다."
            );
            return;
        }

        // Spring Security에 인증 정보 등록
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        null,
                        java.util.Collections.emptyList()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
