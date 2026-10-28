package com.ceos.plabfootball.domain.favorite.service;

import com.ceos.plabfootball.domain.favorite.dto.response.FavoriteMatchResponse;
import com.ceos.plabfootball.domain.favorite.repository.FavoriteMatchRepository;
import com.ceos.plabfootball.domain.match.repository.MatchRepository;
import com.ceos.plabfootball.domain.user.entity.User;
import com.ceos.plabfootball.domain.user.service.CurrentUserService;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import com.ceos.plabfootball.global.apiPayload.exception.GeneralException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteMatchService {

    private final FavoriteMatchRepository favoriteRepository;
    private final MatchRepository matchRepository;
    private final CurrentUserService currentUserService;

    // 매치 행을 명시적으로 잠그지 않고, 찜 개수는 조회 시점을 기준으로 집계한다.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FavoriteMatchResponse setFavorite(Long matchId, Principal principal, boolean favorite) {
        User user = currentUserService.require(principal);

        if (!matchRepository.existsById(matchId)) {
            throw new GeneralException(ErrorStatus._NOT_FOUND);
        }

        if (favorite) {
            favoriteRepository.insertIfAbsent(user.getId(), matchId);
        } else {
            favoriteRepository.deleteForUser(user.getId(), matchId);
        }

        return FavoriteMatchResponse.builder()
                .matchId(matchId)
                .isFavorite(favorite)
                .favoriteCount(favoriteRepository.countForMatch(matchId))
                .build();
    }
}
