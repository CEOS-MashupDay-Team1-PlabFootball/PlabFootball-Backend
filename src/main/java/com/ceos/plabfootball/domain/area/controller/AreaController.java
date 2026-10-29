package com.ceos.plabfootball.domain.area.controller;

import com.ceos.plabfootball.domain.area.dto.response.AreaResponse;
import com.ceos.plabfootball.domain.area.dto.response.CityResponse;
import com.ceos.plabfootball.domain.area.service.AreaService;
import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "지역", description = "도시와 개별 지역 조회, 게스트별 내 지역 조회")
public class AreaController {

    private final AreaService areaService;

    @GetMapping("/cities")
    @Operation(summary = "도시 목록 조회", description = "인증 없이 조회할 수 있습니다.")
    public ApiResponse<List<CityResponse>> cities() {
        return ApiResponse.onSuccess(areaService.getCities());
    }

    @GetMapping("/cities/{cityId}/areas")
    @Operation(summary = "도시별 지역 목록 조회", description = "인증된 게스트는 자신의 하트 여부를 함께 조회합니다. 비인증 사용자는 모두 false입니다.")
    public ApiResponse<List<AreaResponse>> areas(
            @PathVariable @Positive Long cityId,
            @Parameter(hidden = true) Principal principal
    ) {
        return ApiResponse.onSuccess(areaService.getAreas(cityId, principal));
    }

    @GetMapping("/users/me/areas")
    @Operation(summary = "내 지역 목록 조회", description = "인증 필수. 현재 게스트가 등록한 개별 지역을 반환합니다. 적용 시 선택한 areaId들을 매치 목록 필터로 전달합니다.")
    public ApiResponse<List<AreaResponse>> myAreas(@Parameter(hidden = true) Principal principal) {
        return ApiResponse.onSuccess(areaService.getMyAreas(principal));
    }
}
