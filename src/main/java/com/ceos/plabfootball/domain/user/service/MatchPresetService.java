package com.ceos.plabfootball.domain.user.service;

import com.ceos.plabfootball.domain.user.dto.request.MatchPresetFilters;
import com.ceos.plabfootball.domain.user.dto.request.MatchPresetSaveRequest;
import com.ceos.plabfootball.domain.user.dto.request.MatchPresetUpdateRequest;
import com.ceos.plabfootball.domain.user.dto.response.MatchPresetResponse;
import com.ceos.plabfootball.domain.user.entity.User;
import com.ceos.plabfootball.domain.user.entity.UserFilter;
import com.ceos.plabfootball.domain.user.repository.UserFilterRepository;
import com.ceos.plabfootball.global.apiPayload.code.status.ErrorStatus;
import com.ceos.plabfootball.global.apiPayload.exception.GeneralException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchPresetService {
    // MatchPresetFilters DTO에 정의된 필드명을 추출하여 Set으로 관리
    // 프리셋 수정 요청에서 허용되지 않은 필드가 들어오는 것을 방지
    private static final Set<String> FILTER_FIELDS = Arrays.stream(MatchPresetFilters.class.getRecordComponents())
            .map(component -> component.getName()).collect(Collectors.toUnmodifiableSet());
    private final UserFilterRepository userFilterRepository;
    private final CurrentUserService currentUserService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    // 매치필터 프리셋 생성
    @Transactional
    public MatchPresetResponse create(MatchPresetSaveRequest request, Principal principal) {
        User user = currentUserService.require(principal);
        UserFilter preset = UserFilter.builder()
                .user_id(user)
                .title(request.title())
                .filterSetting(objectMapper.writeValueAsString(request.filters()))
                .isDefault(false)
                .build();
        return toResponse(userFilterRepository.saveAndFlush(preset));
    }

    // 내 매치필터 프리셋 목록 조회
    public List<MatchPresetResponse> list(Principal principal) {
        Long userId = currentUserService.require(principal).getId();
        return userFilterRepository.findAllForUser(userId).stream().map(this::toResponse).toList();
    }

    // 프리셋 하나만 조회(presetId에 해당하는 프리셋)
    // 현재 사용자가 소유한 프리셋만 조회
    public MatchPresetResponse get(Long presetId, Principal principal) {
        Long userId = currentUserService.require(principal).getId();
        return toResponse(findOwned(presetId, userId));
    }

    // 기존 프리셋의 이름이나 필터 조건을 부분 수정
    // 요청에 포함되지 않은 기존 값은 그대로 유지한다.
    @Transactional
    public MatchPresetResponse update(Long presetId, MatchPresetUpdateRequest request, Principal principal) {
        Long userId = currentUserService.require(principal).getId();
        UserFilter preset = findOwned(presetId, userId);
        MatchPresetFilters filters = objectMapper.readValue(preset.getFilterSetting(), MatchPresetFilters.class);
        if (request.hasFilters()) {
            ObjectNode merged = objectMapper.valueToTree(filters);
            for (var entry : request.getFilters().properties()) {
                if (!FILTER_FIELDS.contains(entry.getKey())) {
                    throw new GeneralException(ErrorStatus._BAD_REQUEST);
                }
                merged.set(entry.getKey(), entry.getValue());
            }
            try {
                filters = objectMapper.treeToValue(merged, MatchPresetFilters.class);
            } catch (JacksonException exception) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST);
            }
        }
        String title = request.hasTitle() ? request.getTitle() : preset.getTitle();
        // 병합된 최종 값에도 생성 요청과 동일한 이름 및 필터 검증을 적용한다.
        MatchPresetSaveRequest mergedRequest = new MatchPresetSaveRequest(title, filters);
        var violations = validator.validate(mergedRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        preset.update(mergedRequest.title(), objectMapper.writeValueAsString(filters));
        userFilterRepository.flush();
        return toResponse(preset);
    }

    // 필터 프리셋 수정
    // 현재 사용자가 소유한 프리셋만 삭제 가능하다.
    @Transactional
    public void delete(Long presetId, Principal principal) {
        Long userId = currentUserService.require(principal).getId();
        userFilterRepository.delete(findOwned(presetId, userId));
    }


    private UserFilter findOwned(Long presetId, Long userId) {
        return userFilterRepository.findForUser(presetId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MATCH_PRESET_NOT_FOUND));
    }

    private MatchPresetResponse toResponse(UserFilter preset) {
        return new MatchPresetResponse(
                preset.getId(), preset.getTitle(),
                objectMapper.readValue(preset.getFilterSetting(), MatchPresetFilters.class),
                preset.getCreatedAt(), preset.getUpdatedAt());
    }
}
