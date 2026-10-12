package com.ceos.plabfootball.domain.user.service;

import com.ceos.plabfootball.domain.user.entity.User;
import com.ceos.plabfootball.domain.user.repository.UserRepository;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import com.ceos.plabfootball.global.apiPayload.exception.GeneralException;
import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrentUserService {
    private final UserRepository userRepository;
    private final Clock clock;

    // 인증 계층이 검증한 Principal 이름은 user ID 또는 guest UUID를 사용한다.
    public Optional<User> find(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        String name = principal.getName();
        if (name == null || name.isBlank()) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }
        Optional<User> user;
        try {
            user = userRepository.findById(Long.parseLong(name));
        } catch (NumberFormatException exception) {
            user = userRepository.findByGuestUuid(name);
        }
        return Optional.of(user.filter(value -> value.getExpiresAt().isAfter(LocalDateTime.now(clock)))
                .orElseThrow(() -> new GeneralException(ErrorStatus._UNAUTHORIZED)));
    }

    public User require(Principal principal) {
        return find(principal).orElseThrow(() -> new GeneralException(ErrorStatus._UNAUTHORIZED));
    }
}
