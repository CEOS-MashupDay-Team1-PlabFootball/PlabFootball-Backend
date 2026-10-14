
package com.ceos.plabfootball.global.security.handler;

import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import com.ceos.plabfootball.global.apiPayload.code.ErrorReasonDTO;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthErrorHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // Access Token 없이 보호 API에 접근한 경우
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        sendError(response, ErrorStatus.AUTH_UNAUTHORIZED);
    }

    // 공통 인증 오류 응답 작성
    public void sendError(
            HttpServletResponse response,
            ErrorStatus errorStatus
    ) throws IOException {

        ErrorReasonDTO reason = errorStatus.getReasonHttpStatus();

        response.setStatus(reason.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> body = ApiResponse.onFailure(
                reason.getCode(),
                reason.getMessage(),
                null
        );

        response.getWriter().write(
                objectMapper.writeValueAsString(body)
        );
    }
}
