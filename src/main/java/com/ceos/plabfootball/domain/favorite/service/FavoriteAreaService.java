package com.ceos.plabfootball.domain.favorite.service;

import com.ceos.plabfootball.domain.area.repository.AreaRepository;
import com.ceos.plabfootball.domain.favorite.dto.response.FavoriteAreaResponse;
import com.ceos.plabfootball.domain.favorite.repository.FavoriteAreaRepository;
import com.ceos.plabfootball.domain.user.service.CurrentUserService;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import com.ceos.plabfootball.global.apiPayload.exception.GeneralException;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteAreaService {

    private final AreaRepository areaRepository;
    private final FavoriteAreaRepository favoriteAreaRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public FavoriteAreaResponse setFavorite(Long areaId, Principal principal, boolean favorite) {
        Long userId = currentUserService.require(principal).getId();
        if (!areaRepository.existsById(areaId)) {
            throw new GeneralException(ErrorStatus._NOT_FOUND);
        }

        if (favorite) {
            favoriteAreaRepository.insertIfAbsent(userId, areaId);
        } else {
            favoriteAreaRepository.deleteForUser(userId, areaId);
        }

        return FavoriteAreaResponse.builder().areaId(areaId).isFavorite(favorite).build();
    }
}
