package com.ceos.plabfootball.domain.match.controller;

import com.ceos.plabfootball.domain.match.dto.request.MatchListRequest;
import com.ceos.plabfootball.domain.match.dto.response.MatchListResponse;
import com.ceos.plabfootball.domain.match.service.MatchService;
import com.ceos.plabfootball.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
@Tag(name = "매치 목록")
public class MatchController {
    private final MatchService matchService;
    @Value("${app.images.base-url:}")
    private String imageBaseUrl;

    @GetMapping
    @Operation(summary = "날짜별 매치 목록 조회", description = "공개 조회. 시작 시각 및 ID 오름차순. 인증된 사용자는 내 찜 여부 포함")
    public ApiResponse<MatchListResponse> list(@Valid @ModelAttribute @ParameterObject MatchListRequest request,
                                              @Parameter(hidden = true) Principal principal) {
        String base = imageBaseUrl.isBlank()
                ? ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() : imageBaseUrl;
        return ApiResponse.onSuccess(matchService.list(request, principal, base.replaceAll("/+$", "")));
    }
}
