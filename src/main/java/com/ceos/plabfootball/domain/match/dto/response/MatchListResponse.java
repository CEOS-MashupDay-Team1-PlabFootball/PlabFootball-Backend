package com.ceos.plabfootball.domain.match.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MatchListResponse(List<MatchListItemResponse> content, int page, int size,
                                boolean hasNext, long totalElements, int totalPages) {}
