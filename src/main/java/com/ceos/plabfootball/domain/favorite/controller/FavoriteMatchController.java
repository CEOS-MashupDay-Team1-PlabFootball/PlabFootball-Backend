package com.ceos.plabfootball.domain.favorite.controller;

import com.ceos.plabfootball.domain.favorite.dto.response.FavoriteMatchResponse;
import com.ceos.plabfootball.domain.favorite.service.FavoriteMatchService;
import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches/{matchId}/favorite")
@Tag(name = "매치 찜")
public class FavoriteMatchController {
    private final FavoriteMatchService favoriteMatchService;

    @PutMapping
    @Operation(summary = "매치 찜 등록", description = "인증 필수. 이미 찜한 경우에도 성공. 마감·취소·지난 매치도 가능")
    public ApiResponse<FavoriteMatchResponse> add(@PathVariable Long matchId, @Parameter(hidden = true) Principal principal) {
        return ApiResponse.onSuccess(favoriteMatchService.setFavorite(matchId, principal, true));
    }

    @DeleteMapping
    @Operation(summary = "매치 찜 해제", description = "인증 필수. 이미 해제한 경우에도 성공")
    public ApiResponse<FavoriteMatchResponse> remove(@PathVariable Long matchId, @Parameter(hidden = true) Principal principal) {
        return ApiResponse.onSuccess(favoriteMatchService.setFavorite(matchId, principal, false));
    }
}
