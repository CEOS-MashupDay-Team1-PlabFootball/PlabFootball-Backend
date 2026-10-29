package com.ceos.plabfootball.domain.user.controller;

import com.ceos.plabfootball.domain.user.dto.request.MatchPresetSaveRequest;
import com.ceos.plabfootball.domain.user.dto.request.MatchPresetUpdateRequest;
import com.ceos.plabfootball.domain.user.dto.response.MatchPresetResponse;
import com.ceos.plabfootball.domain.user.service.MatchPresetService;
import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/match-presets")
@Tag(name = "내 매치 프리셋", description = "인증된 게스트 본인의 매치 필터를 임시 저장하고 다시 불러옵니다.")
public class MatchPresetController {
    private final MatchPresetService matchPresetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "매치 프리셋 임시 저장", description = "이름을 붙여 새 프리셋을 저장합니다. 작성 중인 빈 필터도 저장할 수 있습니다.")
    public ApiResponse<MatchPresetResponse> create(
            @Valid @RequestBody MatchPresetSaveRequest request,
            @Parameter(hidden = true) Principal principal
    ) {
        return ApiResponse.onSuccess(matchPresetService.create(request, principal));
    }

    @GetMapping
    @Operation(summary = "내 매치 프리셋 목록 조회")
    public ApiResponse<List<MatchPresetResponse>> list(@Parameter(hidden = true) Principal principal) {
        return ApiResponse.onSuccess(matchPresetService.list(principal));
    }

    @GetMapping("/{presetId}")
    @Operation(summary = "내 매치 프리셋 불러오기")
    public ApiResponse<MatchPresetResponse> get(
            @PathVariable @Positive Long presetId,
            @Parameter(hidden = true) Principal principal
    ) {
        return ApiResponse.onSuccess(matchPresetService.get(presetId, principal));
    }

    @PatchMapping("/{presetId}")
    @Operation(summary = "내 매치 프리셋 부분 수정", description = "생략한 이름과 필터는 유지합니다. filters 안의 null은 해당 조건을 해제합니다.")
    public ApiResponse<MatchPresetResponse> update(
            @PathVariable @Positive Long presetId,
            @Valid @RequestBody MatchPresetUpdateRequest request,
            @Parameter(hidden = true) Principal principal
    ) {
        return ApiResponse.onSuccess(matchPresetService.update(presetId, request, principal));
    }

    @DeleteMapping("/{presetId}")
    @Operation(summary = "내 매치 프리셋 삭제", description = "현재 게스트 본인의 프리셋만 삭제합니다.")
    public ApiResponse<Void> delete(
            @PathVariable @Positive Long presetId,
            @Parameter(hidden = true) Principal principal
    ) {
        matchPresetService.delete(presetId, principal);
        return ApiResponse.onSuccess(null);
    }
}
