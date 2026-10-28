package com.ceos.plabfootball.domain.match.service;

import com.ceos.plabfootball.domain.favorite.repository.FavoriteMatchRepository;
import com.ceos.plabfootball.domain.match.dto.request.MatchListRequest;
import com.ceos.plabfootball.domain.match.dto.response.MatchListItemResponse;
import com.ceos.plabfootball.domain.match.dto.response.MatchListResponse;
import com.ceos.plabfootball.domain.match.entity.Match;
import com.ceos.plabfootball.domain.match.repository.MatchRepository;
import com.ceos.plabfootball.domain.match.repository.MatchSpecification;
import com.ceos.plabfootball.domain.user.service.CurrentUserService;
import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        Long userId = currentUserService.find(principal)
                .map(user -> user.getId())
                .orElse(null);

        // 필터와 응답의 임박 상태가 일치하도록 요청당 한 번 구한 시각을 공유한다.
        LocalDateTime now = LocalDateTime.now(clock);
        PageRequest pageRequest = PageRequest.of(
                request.getPage(), request.getSize(), Sort.by("startTime", "id"));

        Page<Match> page = matchRepository.findAll(
                MatchSpecification.search(request, now), pageRequest);

        List<Long> matchIds = page.getContent().stream()
                .map(Match::getId)
                .toList();

        // 매치마다 조회하지 않고 현재 페이지의 찜 개수를 한 번에 집계한다.
        Map<Long, Long> favoriteCounts = matchIds.isEmpty()
                ? Map.of()
                : favoriteRepository.countByMatchIds(matchIds).stream()
                        .collect(Collectors.toMap(
                                FavoriteMatchRepository.FavoriteCount::getMatchId,
                                FavoriteMatchRepository.FavoriteCount::getFavoriteCount));

        // 공개 조회에서는 내 찜 정보가 없으므로 사용자별 조회를 생략한다.
        Set<Long> favoriteMatchIds = matchIds.isEmpty() || userId == null
                ? Set.of()
                : new HashSet<>(favoriteRepository.findFavoriteMatchIds(userId, matchIds));

        List<MatchListItemResponse> content = page.getContent().stream()
                .map(match -> MatchListItemResponse.from(
                        match,
                        MatchAvailability.from(match, now),
                        favoriteMatchIds.contains(match.getId()),
                        favoriteCounts.getOrDefault(match.getId(), 0L),
                        imageBaseUrl))
                .toList();

        return MatchListResponse.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .hasNext(page.hasNext())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
