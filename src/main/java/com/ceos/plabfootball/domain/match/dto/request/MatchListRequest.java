package com.ceos.plabfootball.domain.match.dto.request;

import com.ceos.plabfootball.domain.match.enums.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "같은 타입/레벨/지역/상태/기타 항목은 OR, 시설 및 서로 다른 항목은 AND. 미선택 조건은 제한 없음")
public class MatchListRequest {
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "조회 날짜(KST)", example = "2026-10-12", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Size(max = 100)
    private List<@NotNull @Positive Long> areaId;
    @Positive
    private Long cityId;
    @Min(0) @Max(23)
    @Schema(description = "시작 시간 하한(포함), 예: 18시 이후", example = "18")
    private Integer timeAfter;
    @Size(max = 3)
    private List<@NotNull MatchType> matchType;
    private GenderType gender;
    @Size(max = 3)
    private List<@NotNull LevelType> level;
    @Size(max = 2)
    @ArraySchema(schema = @Schema(type = "string", allowableValues = {"CONFIRMING_SOON", "CLOSING_SOON"}))
    private List<@NotNull MatchStatus> status;
    @Schema(description = "true: 실내, false: 실외, 생략: 제한 없음")
    private Boolean indoor;
    private Boolean awning;
    private Boolean parking;
    private Boolean shoeRental;
    private Boolean earlybird;
    @Size(max = 2)
    @Schema(description = "AI_REPORT와 SCREEN을 함께 선택하면 OR")
    private List<@NotNull MatchFeature> feature;
    @Builder.Default
    @Schema(description = "마감, 취소, 이미 시작한 매치 제외", defaultValue = "true")
    private boolean hideClosed = true;
    @Builder.Default
    @Min(0) @Max(10000)
    private int page = 0;
    @Builder.Default
    @Min(1) @Max(100)
    private int size = 20;

    // 전체 매치 상태를 재사용하되, 검색 조건은 화면에서 제공하는 두 값으로 제한한다.
    @AssertTrue(message = "status에는 CONFIRMING_SOON과 CLOSING_SOON만 사용할 수 있습니다.")
    @Schema(hidden = true)
    public boolean isStatusSupported() {
        return status == null || status.stream().allMatch(value ->
                value == MatchStatus.CONFIRMING_SOON || value == MatchStatus.CLOSING_SOON);
    }
}
