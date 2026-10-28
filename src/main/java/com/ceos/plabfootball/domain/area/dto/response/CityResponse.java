package com.ceos.plabfootball.domain.area.dto.response;

import com.ceos.plabfootball.domain.area.entity.City;
import lombok.Builder;

@Builder
public record CityResponse(Long cityId, String name) {
    public static CityResponse from(City city) {
        return CityResponse.builder().cityId(city.getId()).name(city.getName()).build();
    }
}
