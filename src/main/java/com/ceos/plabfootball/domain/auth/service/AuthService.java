package com.ceos.plabfootball.domain.auth.service;

import com.ceos.plabfootball.domain.auth.dto.TokenPair;
import com.ceos.plabfootball.domain.user.entity.User;
import com.ceos.plabfootball.domain.user.repository.UserRepository;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import com.ceos.plabfootball.global.apiPayload.exception.GeneralException;
import com.ceos.plabfootball.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    // 새로운 게스트 생성
    @Transactional
    public TokenPair guestLogin() {

        // 1. 게스트 사용자 생성
        User guest = User.createGuest();

        // 2. DB 저장
        User savedGuest = userRepository.save(guest);

        // 3. Access Token 발급
        String accessToken =
                jwtProvider.createAccessToken(savedGuest.getId());

        // 4. Refresh Token 발급
        String refreshToken =
                jwtProvider.createRefreshToken(savedGuest.getId());

        // 5. 두 토큰 반환
        return new TokenPair(
                savedGuest.getId(),
                accessToken,
                refreshToken
        );
    }


    @Transactional
    public TokenPair refresh(String refreshToken) {

        // 1. Refresh Token 유효성 및 타입 검증
        if (refreshToken == null
                || !jwtProvider.validateToken(refreshToken)
                || !jwtProvider.isRefreshToken(refreshToken)) {

            throw new GeneralException(
                    ErrorStatus.AUTH_INVALID_REFRESH_TOKEN
            );
        }

        // 2. Refresh Token에서 userId 추출
        Long userId;
        try {
            userId = jwtProvider.getUserId(refreshToken);
        } catch (RuntimeException e) {
            throw new GeneralException(
                    ErrorStatus.AUTH_INVALID_REFRESH_TOKEN
            );
        }

        // 3. 기존 게스트 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(
                        ErrorStatus.AUTH_INVALID_GUEST
                ));

        // 4. 게스트 보관 기간 확인
        if (user.isExpired()) {
            throw new GeneralException(
                    ErrorStatus.AUTH_INVALID_GUEST
            );
        }

        // 5. 게스트 보관 기간 7일 연장
        user.extendExpiration();

        // 6. 새로운 Access Token 및 Refresh Token 발급
        String newAccessToken =
                jwtProvider.createAccessToken(user.getId());

        String newRefreshToken =
                jwtProvider.createRefreshToken(user.getId());

        // 7. 기존 userId와 새로운 토큰 반환
        return new TokenPair(
                user.getId(),
                newAccessToken,
                newRefreshToken
        );
    }

}