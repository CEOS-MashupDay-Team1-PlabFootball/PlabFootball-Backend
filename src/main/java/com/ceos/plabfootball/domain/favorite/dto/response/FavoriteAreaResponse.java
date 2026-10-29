package com.ceos.plabfootball.domain.favorite.dto.response;

import lombok.Builder;

@Builder
public record FavoriteAreaResponse(Long areaId, boolean isFavorite) {}
