
package com.ceos.plabfootball.global.security.handler;

import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthErrorHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // 인증 정보 없이 보호 API에 접근한 경우
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        sendError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "AUTH_UNAUTHORIZED",
                "인증이 필요한 요청입니다."
        );
    }

    // 공통 인증 오류 응답 작성
    public void sendError(
            HttpServletResponse response,
            int status,
            String code,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> body =
                ApiResponse.onFailure(code, message, null);

        objectMapper.writeValue(response.getWriter(), body);
    }
}
