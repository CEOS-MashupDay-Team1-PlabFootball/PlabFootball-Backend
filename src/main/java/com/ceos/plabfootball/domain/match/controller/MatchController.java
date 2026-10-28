package com.ceos.plabfootball.domain.match.controller;

import com.ceos.plabfootball.domain.match.dto.request.MatchListRequest;
import com.ceos.plabfootball.domain.match.dto.response.MatchListResponse;
import com.ceos.plabfootball.domain.match.service.MatchService;
import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import com.ceos.plabfootball.global.apiPayload.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "매치 목록")
public class MatchController {

    private final MatchService matchService;
    private final String imageBaseUrl;

    public MatchController(MatchService matchService, @Value("${app.images.base-url}") String imageBaseUrl) {
        if (imageBaseUrl == null || imageBaseUrl.isBlank()) {
            throw new IllegalArgumentException("app.images.base-url 설정이 필요합니다.");
        }

        URI uri = URI.create(imageBaseUrl);
        boolean http = "http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme());
        if (!http || uri.getHost() == null || uri.getUserInfo() != null
                || uri.getRawQuery() != null || uri.getRawFragment() != null) {
            throw new IllegalArgumentException("app.images.base-url에는 HTTP(S) 서버 주소를 설정해야 합니다.");
        }

        this.matchService = matchService;
        this.imageBaseUrl = imageBaseUrl.replaceAll("/+$", "");
    }

    @GetMapping
    @Operation(summary = "날짜별 매치 목록 조회", description = "공개 조회. 시작 시각 및 ID 오름차순. 인증된 사용자는 내 찜 여부 포함")
    public ApiResponse<MatchListResponse> list(@Valid @ModelAttribute @ParameterObject MatchListRequest request,
                                              @Parameter(hidden = true) Principal principal) {
        return ApiResponse.onSuccess(matchService.list(request, principal, imageBaseUrl));
    }

    @PostMapping("/search")
    @Operation(summary = "필터 조건 기반 매치 목록 조회", description = "날짜 외 추가 필터가 하나 이상 필요합니다. 실내·그늘막·주차는 true일 때만 적용합니다.미선택 조건은 제외.")
    public ApiResponse<MatchListResponse> search(
            @Valid @RequestBody MatchListRequest request,
            @Parameter(hidden = true) Principal principal
    ) {
        if (!request.hasAdditionalFilter()) {
            throw new GeneralException(ErrorStatus.MATCH_FILTER_REQUIRED);
        }
        return ApiResponse.onSuccess(
                matchService.list(request, principal, imageBaseUrl)
        );
    }
}
