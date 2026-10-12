package com.ceos.plabfootball.domain.match.service;

import com.ceos.plabfootball.domain.match.entity.Match;
import com.ceos.plabfootball.domain.match.enums.MatchStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MatchAvailability(boolean canApply, boolean closingSoon, boolean confirmingSoon,
                                int remainingSlots, MatchStatus status) {
    public static final int CLOSING_SOON_HOURS = 1;
    public static final int CONFIRMING_SOON_REMAINING = 2;

    public static MatchAvailability from(Match match, LocalDateTime now) {
        int remaining = Math.max(0, match.getMaxPlayers() - match.getCurrentPlayers());
        boolean cancelled = match.getStatusType() == MatchStatus.CANCELLED;
        boolean canApply = !cancelled && match.getStatusType() != MatchStatus.CLOSED
                && match.getStartTime().isAfter(now) && remaining > 0;
        boolean closingSoon = canApply && !match.getStartTime().isAfter(now.plusHours(CLOSING_SOON_HOURS));
        int missing = match.getMinPlayers() - match.getCurrentPlayers();
        boolean confirmingSoon = canApply && missing >= 1 && missing <= CONFIRMING_SOON_REMAINING;
        MatchStatus status = cancelled ? MatchStatus.CANCELLED : !canApply ? MatchStatus.CLOSED
                : closingSoon ? MatchStatus.CLOSING_SOON
                : confirmingSoon ? MatchStatus.CONFIRMING_SOON : MatchStatus.OPEN;
        return MatchAvailability.builder().canApply(canApply).closingSoon(closingSoon)
                .confirmingSoon(confirmingSoon).remainingSlots(remaining).status(status).build();
    }
}
