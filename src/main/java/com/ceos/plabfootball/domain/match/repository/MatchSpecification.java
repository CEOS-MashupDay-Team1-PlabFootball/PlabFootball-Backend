package com.ceos.plabfootball.domain.match.repository;

import com.ceos.plabfootball.domain.match.dto.request.MatchListRequest;
import com.ceos.plabfootball.domain.match.entity.Match;
import com.ceos.plabfootball.domain.match.enums.*;
import com.ceos.plabfootball.domain.match.service.MatchAvailability;
import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class MatchSpecification {
    private MatchSpecification() {}

    public static Specification<Match> search(MatchListRequest request, LocalDateTime now) {
        return (root, query, cb) -> {
            // to-one 관계만 fetch하므로 페이지 제한과 행 수가 유지된다.
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("stadium_id").fetch("stadiumGroup_id").fetch("area_id").fetch("city_id");
            }
            Path<?> stadium = root.get("stadium_id");
            Path<?> area = stadium.get("stadiumGroup_id").get("area_id");
            List<Predicate> predicates = new ArrayList<>();
            LocalDateTime start = request.getDate().atStartOfDay();
            if (request.getTimeAfter() != null) {
                start = request.getDate().atTime(request.getTimeAfter(), 0);
            }
            predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), start));
            predicates.add(cb.lessThan(root.get("startTime"), request.getDate().plusDays(1).atStartOfDay()));
            if (request.getCityId() != null) predicates.add(cb.equal(area.get("city_id").get("id"), request.getCityId()));
            in(predicates, area.get("id"), request.getAreaId());
            in(predicates, root.get("matchType"), request.getMatchType());
            in(predicates, root.get("levelType"), request.getLevel());
            if (request.getGender() != null) predicates.add(cb.equal(root.get("genderType"), request.getGender()));
            bool(predicates, cb, stadium.get("isIndoor"), request.getIndoor());
            bool(predicates, cb, stadium.get("hasAwning"), request.getAwning());
            bool(predicates, cb, stadium.get("hasParking"), request.getParking());
            bool(predicates, cb, stadium.get("hasShoeRental"), request.getShoeRental());
            bool(predicates, cb, root.get("isEarlybird"), request.getEarlybird());
            if (request.getFeature() != null && !request.getFeature().isEmpty()) {
                List<Predicate> features = new ArrayList<>();
                if (request.getFeature().contains(MatchFeature.AI_REPORT)) features.add(cb.isTrue(root.get("hasAiReport")));
                if (request.getFeature().contains(MatchFeature.SCREEN)) features.add(cb.isTrue(root.get("hasScreen")));
                predicates.add(cb.or(features.toArray(Predicate[]::new)));
            }
            Predicate available = cb.and(
                    cb.greaterThan(root.get("startTime"), now),
                    cb.lessThan(root.get("currentPlayers"), root.get("maxPlayers")),
                    cb.not(root.get("statusType").in(MatchStatus.CLOSED, MatchStatus.CANCELLED)));
            if (request.isHideClosed()) predicates.add(available);
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                List<Predicate> states = new ArrayList<>();
                if (request.getStatus().contains(MatchFilterStatus.CLOSING_SOON)) {
                    states.add(cb.lessThanOrEqualTo(root.get("startTime"), now.plusHours(MatchAvailability.CLOSING_SOON_HOURS)));
                }
                if (request.getStatus().contains(MatchFilterStatus.CONFIRMING_SOON)) {
                    states.add(cb.between(cb.diff(root.<Integer>get("minPlayers"), root.<Integer>get("currentPlayers")),
                            1, MatchAvailability.CONFIRMING_SOON_REMAINING));
                }
                predicates.add(cb.and(available, cb.or(states.toArray(Predicate[]::new))));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static void in(List<Predicate> predicates, Path<?> path, List<?> values) {
        if (values != null && !values.isEmpty()) predicates.add(path.in(values));
    }

    private static void bool(List<Predicate> predicates, CriteriaBuilder cb, Path<Boolean> path, Boolean value) {
        if (value != null) predicates.add(cb.equal(path, value));
    }
}
