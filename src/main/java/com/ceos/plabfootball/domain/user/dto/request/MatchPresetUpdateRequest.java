package com.ceos.plabfootball.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import tools.jackson.databind.node.ObjectNode;

@Schema(description = "생략한 값은 유지합니다. filters 안의 null은 해당 조건을 해제합니다.")
public class MatchPresetUpdateRequest {
    private String title;
    private ObjectNode filters;
    private boolean titleProvided;
    private boolean filtersProvided;

    public String getTitle() {
        return title;
    }

    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
        this.titleProvided = true;
    }

    @Schema(implementation = MatchPresetFilters.class)
    public ObjectNode getFilters() {
        return filters;
    }

    @JsonSetter("filters")
    public void setFilters(ObjectNode filters) {
        this.filters = filters;
        this.filtersProvided = true;
    }

    public boolean hasTitle() {
        return titleProvided;
    }

    public boolean hasFilters() {
        return filtersProvided;
    }

    @AssertTrue(message = "수정할 이름 또는 필터를 하나 이상 전달해 주세요.")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isChangeRequested() {
        return titleProvided || (filtersProvided && filters != null && !filters.isEmpty());
    }

    @AssertTrue(message = "filters는 null이 아닌 JSON 객체여야 합니다.")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isFiltersValid() {
        return !filtersProvided || filters != null;
    }
}
