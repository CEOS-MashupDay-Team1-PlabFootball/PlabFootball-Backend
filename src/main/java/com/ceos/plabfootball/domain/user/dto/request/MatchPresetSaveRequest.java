package com.ceos.plabfootball.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MatchPresetSaveRequest(
        @NotBlank @Size(max = 10, message = "프리셋 제목은 10자 이내로 입력해 주세요.")
        @Schema(description = "생략, null, 빈 문자열 또는 공백이면 기본 이름 적용", defaultValue = "내 필터", example = "퇴근 후 풋살")
        String title,
        @NotNull @Valid MatchPresetFilters filters
) {
    public MatchPresetSaveRequest {
        title = title == null || title.isBlank() ? "내 필터" : title.strip();
    }
}
