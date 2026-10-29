package com.ceos.plabfootball.domain.user.dto.response;

import com.ceos.plabfootball.domain.user.dto.request.MatchPresetFilters;
import java.time.LocalDateTime;

public record MatchPresetResponse(
        Long presetId,
        String title,
        MatchPresetFilters filters,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
