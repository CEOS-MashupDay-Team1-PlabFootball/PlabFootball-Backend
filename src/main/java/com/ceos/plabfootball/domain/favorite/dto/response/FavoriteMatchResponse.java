package com.ceos.plabfootball.domain.favorite.dto.response;

import lombok.Builder;

@Builder
public record FavoriteMatchResponse(Long matchId, boolean isFavorite, long favoriteCount) {}
