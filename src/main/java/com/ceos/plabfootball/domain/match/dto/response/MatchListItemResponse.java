package com.ceos.plabfootball.domain.match.dto.response;

import com.ceos.plabfootball.domain.area.entity.Area;
import com.ceos.plabfootball.domain.match.entity.Match;
import com.ceos.plabfootball.domain.match.enums.*;
import com.ceos.plabfootball.domain.match.service.MatchAvailability;
import com.ceos.plabfootball.domain.stadium.entity.Stadium;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.Builder;

@Builder
public record MatchListItemResponse(
        Long matchId, OffsetDateTime startTime, OffsetDateTime endTime, Integer price,
        Long stadiumId, String stadiumName, String stadiumAddress, String stadiumImageUrl,
        Long areaId, String areaName, Long cityId, String cityName,
        MatchType matchType, GenderType genderType, LevelType levelType, MatchStatus status,
        int currentPlayers, int minPlayers, int maxPlayers, int remainingSlots,
        boolean canApply, boolean isClosingSoon, boolean isConfirmingSoon,
        Boolean isIndoor, Boolean hasAwning, Boolean hasParking, Boolean hasShoeRental,
        Integer parkingCapacity, Integer reservedParkingCount,
        Boolean isEarlybird, Boolean hasAiReport, Boolean hasScreen,
        boolean isFavorite, long favoriteCount) {

    public static MatchListItemResponse from(Match match, MatchAvailability availability,
                                            boolean favorite, long favoriteCount, String imageBaseUrl) {
        Stadium stadium = match.getStadium_id();
        Area area = stadium.getStadiumGroup_id().getArea_id();
        String imageUrl = stadium.getImageUrl();
        if (imageUrl != null && imageUrl.startsWith("/") && !imageUrl.startsWith("//")) {
            imageUrl = imageBaseUrl + imageUrl;
        }
        ZoneId zone = ZoneId.of("Asia/Seoul");
        return MatchListItemResponse.builder()
                .matchId(match.getId()).startTime(match.getStartTime().atZone(zone).toOffsetDateTime())
                .endTime(match.getEndTime().atZone(zone).toOffsetDateTime()).price(match.getPrice())
                .stadiumId(stadium.getId()).stadiumName(stadium.getName()).stadiumAddress(stadium.getAddress())
                .stadiumImageUrl(imageUrl).areaId(area.getId()).areaName(area.getName())
                .cityId(area.getCity_id().getId()).cityName(area.getCity_id().getName())
                .matchType(match.getMatchType()).genderType(match.getGenderType()).levelType(match.getLevelType())
                .status(availability.status()).currentPlayers(match.getCurrentPlayers())
                .minPlayers(match.getMinPlayers()).maxPlayers(match.getMaxPlayers())
                .remainingSlots(availability.remainingSlots()).canApply(availability.canApply())
                .isClosingSoon(availability.closingSoon()).isConfirmingSoon(availability.confirmingSoon())
                .isIndoor(stadium.getIsIndoor()).hasAwning(stadium.getHasAwning())
                .hasParking(stadium.getHasParking()).hasShoeRental(stadium.getHasShoeRental())
                .parkingCapacity(match.getParkingCapacity()).reservedParkingCount(match.getReservedParkingCount())
                .isEarlybird(match.getIsEarlybird()).hasAiReport(match.getHasAiReport()).hasScreen(match.getHasScreen())
                .isFavorite(favorite).favoriteCount(favoriteCount).build();
    }
}
