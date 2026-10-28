package com.ceos.plabfootball.domain.area.dto.response;

import com.ceos.plabfootball.domain.area.entity.Area;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AreaResponse(
        @Schema(description = "매치 목록의 areaId 필터에 전달할 실제 지역 ID") Long areaId,
        String name,
        Long cityId,
        String cityName,
        boolean isFavorite
) {
    public static AreaResponse from(Area area, boolean favorite) {
        return AreaResponse.builder()
                .areaId(area.getId())
                .name(area.getName())
                .cityId(area.getCity_id().getId())
                .cityName(area.getCity_id().getName())
                .isFavorite(favorite)
                .build();
    }
}
