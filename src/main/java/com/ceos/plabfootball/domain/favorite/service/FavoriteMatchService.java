package com.ceos.plabfootball.domain.favorite.service;

import com.ceos.plabfootball.domain.favorite.dto.response.FavoriteMatchResponse;
import com.ceos.plabfootball.domain.favorite.entity.FavoriteMatch;
import com.ceos.plabfootball.domain.favorite.repository.FavoriteMatchRepository;
import com.ceos.plabfootball.domain.match.entity.Match;
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

    // 같은 매치의 등록/해제를 직렬화하고 잠금을 얻은 뒤 최신 찜 상태를 읽는다.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FavoriteMatchResponse setFavorite(Long matchId, Principal principal, boolean favorite) {
        User user = currentUserService.require(principal);
        Match match = matchRepository.findForUpdate(matchId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND));
        if (favorite) {
            if (!favoriteRepository.existsForUser(user.getId(), matchId)) {
                favoriteRepository.saveAndFlush(FavoriteMatch.builder().user_id(user).match_id(match).build());
            }
        } else {
            favoriteRepository.deleteForUser(user.getId(), matchId);
        }
        return FavoriteMatchResponse.builder().matchId(matchId).isFavorite(favorite)
                .favoriteCount(favoriteRepository.countForMatch(matchId)).build();
    }
}
