package com.ceos.plabfootball.domain.match.service;

import com.ceos.plabfootball.domain.favorite.repository.FavoriteMatchRepository;
import com.ceos.plabfootball.domain.match.dto.request.MatchListRequest;
import com.ceos.plabfootball.domain.match.dto.response.*;
import com.ceos.plabfootball.domain.match.entity.Match;
import com.ceos.plabfootball.domain.match.repository.*;
import com.ceos.plabfootball.domain.user.service.CurrentUserService;
import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {
    private final MatchRepository matchRepository;
    private final FavoriteMatchRepository favoriteRepository;
    private final CurrentUserService currentUserService;
    private final Clock clock;

    public MatchListResponse list(MatchListRequest request, Principal principal, String imageBaseUrl) {
        Long userId = currentUserService.find(principal).map(user -> user.getId()).orElse(null);
        LocalDateTime now = LocalDateTime.now(clock);
        Page<Match> page = matchRepository.findAll(MatchSpecification.search(request, now),
                PageRequest.of(request.getPage(), request.getSize(), Sort.by("startTime", "id")));
        List<Long> ids = page.getContent().stream().map(Match::getId).toList();
        Map<Long, Long> counts = ids.isEmpty() ? Map.of() : favoriteRepository.countByMatchIds(ids).stream()
                .collect(Collectors.toMap(FavoriteMatchRepository.FavoriteCount::getMatchId,
                        FavoriteMatchRepository.FavoriteCount::getFavoriteCount));
        Set<Long> favorites = ids.isEmpty() || userId == null ? Set.of()
                : new HashSet<>(favoriteRepository.findFavoriteMatchIds(userId, ids));
        List<MatchListItemResponse> content = page.getContent().stream()
                .map(match -> MatchListItemResponse.from(match, MatchAvailability.from(match, now),
                        favorites.contains(match.getId()), counts.getOrDefault(match.getId(), 0L), imageBaseUrl))
                .toList();
        return MatchListResponse.builder().content(content).page(page.getNumber()).size(page.getSize())
                .hasNext(page.hasNext()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).build();
    }
}
