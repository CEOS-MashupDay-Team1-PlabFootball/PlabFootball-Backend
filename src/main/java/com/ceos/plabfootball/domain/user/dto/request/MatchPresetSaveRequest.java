package com.ceos.plabfootball.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MatchPresetSaveRequest(
        @NotBlank @Size(max = 10) @Schema(example = "퇴근 후 풋살") String title,
        @NotNull @Valid MatchPresetFilters filters
) {
}
