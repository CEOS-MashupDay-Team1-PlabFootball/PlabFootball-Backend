package com.ceos.plabfootball.domain.match.service;

import com.ceos.plabfootball.domain.match.entity.Match;
import com.ceos.plabfootball.domain.match.enums.MatchStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MatchAvailability(
        boolean canApply,
        boolean closingSoon,
        boolean confirmingSoon,
        int remainingSlots,
        MatchStatus status
) {

    public static final int CLOSING_SOON_HOURS = 1;
    public static final int CONFIRMING_SOON_REMAINING = 2;

    public static MatchAvailability from(Match match, LocalDateTime now) {
        int remaining = Math.max(0, match.getMaxPlayers() - match.getCurrentPlayers());
        boolean cancelled = match.getStatusType() == MatchStatus.CANCELLED;

        boolean canApply = !cancelled
                && match.getStatusType() != MatchStatus.CLOSED
                && match.getStartTime().isAfter(now)
                && remaining > 0;

        int missing = match.getMinPlayers() - match.getCurrentPlayers();
        boolean confirmingSoon = canApply
                && missing >= 1
                && missing <= CONFIRMING_SOON_REMAINING;

        // 정확히 1시간 남은 매치도 포함하되, 이미 시작한 매치는 canApply에서 제외한다.
        boolean closingSoon = canApply
                && !match.getStartTime().isAfter(now.plusHours(CLOSING_SOON_HOURS));

        // 취소 상태를 우선 보존한다. 두 임박 조건이 겹치면 대표 상태는 확정임박이다.
        // 개별 boolean은 모두 반환하므로 두 조건의 충족 여부도 확인할 수 있다.
        MatchStatus status;
        if (cancelled) {
            status = MatchStatus.CANCELLED;
        } else if (!canApply) {
            status = MatchStatus.CLOSED;
        } else if (confirmingSoon) {
            status = MatchStatus.CONFIRMING_SOON;
        } else if (closingSoon) {
            status = MatchStatus.CLOSING_SOON;
        } else {
            status = MatchStatus.OPEN;
        }

        return MatchAvailability.builder()
                .canApply(canApply)
                .closingSoon(closingSoon)
                .confirmingSoon(confirmingSoon)
                .remainingSlots(remaining)
                .status(status)
                .build();
    }
}
