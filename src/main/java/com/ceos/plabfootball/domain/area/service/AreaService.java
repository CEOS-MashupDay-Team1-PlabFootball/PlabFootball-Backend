package com.ceos.plabfootball.domain.area.service;

import com.ceos.plabfootball.domain.area.dto.response.AreaResponse;
import com.ceos.plabfootball.domain.area.dto.response.CityResponse;
import com.ceos.plabfootball.domain.area.repository.AreaRepository;
import com.ceos.plabfootball.domain.area.repository.CityRepository;
import com.ceos.plabfootball.domain.favorite.repository.FavoriteAreaRepository;
import com.ceos.plabfootball.domain.user.service.CurrentUserService;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import com.ceos.plabfootball.global.apiPayload.exception.GeneralException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaService {

    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final FavoriteAreaRepository favoriteAreaRepository;
    private final CurrentUserService currentUserService;

    public List<CityResponse> getCities() {
        return cityRepository.findAllByOrderByIdAsc().stream().map(CityResponse::from).toList();
    }

    public List<AreaResponse> getAreas(Long cityId, Principal principal) {
        if (!cityRepository.existsById(cityId)) {
            throw new GeneralException(ErrorStatus._NOT_FOUND);
        }

        // 하트 여부는 한 번에 조회하며, 비인증 사용자에게는 모두 false로 표시한다.
        Set<Long> favorites = currentUserService.find(principal)
                .<Set<Long>>map(user -> new HashSet<>(
                        favoriteAreaRepository.findFavoriteAreaIds(user.getId(), cityId)))
                .orElseGet(Set::of);

        return areaRepository.findAllByCityId(cityId).stream()
                .map(area -> AreaResponse.from(area, favorites.contains(area.getId())))
                .toList();
    }

    public List<AreaResponse> getMyAreas(Principal principal) {
        Long userId = currentUserService.require(principal).getId();
        return favoriteAreaRepository.findAreasForUser(userId).stream()
                .map(area -> AreaResponse.from(area, true))
                .toList();
    }
}
