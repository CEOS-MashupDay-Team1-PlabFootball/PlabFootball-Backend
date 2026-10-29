package com.ceos.plabfootball.domain.user.dto.request;

import com.ceos.plabfootball.domain.match.dto.request.MatchListRequest;
import com.ceos.plabfootball.domain.match.enums.GenderType;
import com.ceos.plabfootball.domain.match.enums.LevelType;
import com.ceos.plabfootball.domain.match.enums.MatchFeature;
import com.ceos.plabfootball.domain.match.enums.MatchStatus;
import com.ceos.plabfootball.domain.match.enums.MatchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "재사용할 매치 필터. 날짜와 페이지는 저장하지 않으며, 작성 중인 빈 조건도 저장할 수 있습니다.")
public record MatchPresetFilters(
        @Positive Long cityId,
        @Size(max = 100) List<@NotNull @Positive Long> areaId,
        @Min(0) @Max(23) Integer timeAfter,
        @Size(max = 3) List<@NotNull MatchType> matchType,
        GenderType gender,
        @Size(max = 3) List<@NotNull LevelType> level,
        @Size(max = 3) List<@NotNull MatchStatus> status,
        @Schema(description = "true일 때만 실내 필터 적용") Boolean indoor,
        @Schema(description = "true일 때만 그늘막 필터 적용") Boolean awning,
        @Schema(description = "true일 때만 주차 필터 적용") Boolean parking,
        Boolean shoeRental,
        Boolean earlybird,
        @Size(max = 2) List<@NotNull MatchFeature> feature,
        @Schema(description = "생략 시 검색에서는 true로 적용") Boolean hideClosed
) {
    public MatchPresetFilters {
        hideClosed = hideClosed == null || hideClosed;
    }

    // 실제 검색 시점의 날짜와 결합하며, 검색 DTO의 필터 의미와 기본값을 그대로 사용한다.
    public MatchListRequest toSearchRequest(LocalDate date) {
        return MatchListRequest.builder()
                .date(date)
                .cityId(cityId).areaId(areaId).timeAfter(timeAfter)
                .matchType(matchType).gender(gender).level(level).status(status)
                .indoor(indoor).awning(awning).parking(parking)
                .shoeRental(shoeRental).earlybird(earlybird).feature(feature)
                .hideClosed(hideClosed == null || hideClosed)
                .build();
    }

    @AssertTrue(message = "status에는 CONFIRMING_SOON과 CLOSING_SOON만 사용할 수 있습니다.")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isStatusSupported() {
        return status == null || status.stream().allMatch(value ->
                value == MatchStatus.CONFIRMING_SOON || value == MatchStatus.CLOSING_SOON);
    }
}
