package com.ceos.plabfootball.domain.favorite.controller;

import com.ceos.plabfootball.domain.favorite.dto.response.FavoriteAreaResponse;
import com.ceos.plabfootball.domain.favorite.service.FavoriteAreaService;
import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/areas/{areaId}/favorite")
@Tag(name = "내 지역 등록")
public class FavoriteAreaController {

    private final FavoriteAreaService favoriteAreaService;

    @PutMapping
    @Operation(summary = "내 지역 등록", description = "인증 필수. 하트를 누를 때 즉시 저장하며, 이미 등록한 경우에도 성공합니다.")
    public ApiResponse<FavoriteAreaResponse> add(
            @PathVariable @Positive Long areaId,
            @Parameter(hidden = true) Principal principal
    ) {
        return ApiResponse.onSuccess(favoriteAreaService.setFavorite(areaId, principal, true));
    }

    @DeleteMapping
    @Operation(summary = "내 지역 해제", description = "인증 필수. 현재 게스트의 지역만 해제하며, 이미 해제한 경우에도 성공합니다.")
    public ApiResponse<FavoriteAreaResponse> remove(
            @PathVariable @Positive Long areaId,
            @Parameter(hidden = true) Principal principal
    ) {
        return ApiResponse.onSuccess(favoriteAreaService.setFavorite(areaId, principal, false));
    }
}
