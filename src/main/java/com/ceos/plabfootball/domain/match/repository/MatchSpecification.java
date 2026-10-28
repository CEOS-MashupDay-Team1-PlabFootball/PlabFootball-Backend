package com.ceos.plabfootball.domain.match.repository;

import com.ceos.plabfootball.domain.match.dto.request.MatchListRequest;
import com.ceos.plabfootball.domain.match.entity.Match;
import com.ceos.plabfootball.domain.match.enums.MatchFeature;
import com.ceos.plabfootball.domain.match.enums.MatchStatus;
import com.ceos.plabfootball.domain.match.service.MatchAvailability;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class MatchSpecification {

    private MatchSpecification() {
    }

    public static Specification<Match> search(MatchListRequest request, LocalDateTime now) {
        return (root, query, cb) -> {
            // 목록에서는 연관 정보를 함께 가져와 추가 조회를 막고, 전체 개수 조회에는 fetch를 적용하지 않는다.
            // to-one 관계만 연결하므로 매치 행이 늘어나지 않아 페이지 제한이 유지된다.
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("stadium_id")
                        .fetch("stadiumGroup_id")
                        .fetch("area_id")
                        .fetch("city_id");
            }

            Path<?> stadium = root.get("stadium_id");
            Path<?> area = stadium.get("stadiumGroup_id").get("area_id");
            List<Predicate> predicates = new ArrayList<>();

            // 조회 날짜의 시작 이상, 다음 날 00시 미만으로 날짜 범위를 한정한다.
            LocalDateTime start = request.getDate().atStartOfDay();
            if (request.getTimeAfter() != null) {
                start = request.getDate().atTime(request.getTimeAfter(), 0);
            }

            predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), start));
            predicates.add(cb.lessThan(root.get("startTime"), request.getDate().plusDays(1).atStartOfDay()));

            if (request.getCityId() != null) {
                predicates.add(cb.equal(area.get("city_id").get("id"), request.getCityId()));
            }

            // 같은 항목의 다중 선택은 IN(OR), 항목끼리는 마지막에 AND로 결합한다.
            in(predicates, area.get("id"), request.getAreaId());
            in(predicates, root.get("matchType"), request.getMatchType());
            in(predicates, root.get("levelType"), request.getLevel());

            if (request.getGender() != null) {
                predicates.add(cb.equal(root.get("genderType"), request.getGender()));
            }

            // 실내, 그늘막, 주차는 true로 선택한 조건만 적용한다.
            enabled(predicates, cb, stadium.get("isIndoor"), request.getIndoor());
            enabled(predicates, cb, stadium.get("hasAwning"), request.getAwning());
            enabled(predicates, cb, stadium.get("hasParking"), request.getParking());
            bool(predicates, cb, stadium.get("hasShoeRental"), request.getShoeRental());
            bool(predicates, cb, root.get("isEarlybird"), request.getEarlybird());

            // 기타 기능은 시설과 달리 AI 리포트 또는 스크린 중 하나만 있어도 포함한다.
            if (request.getFeature() != null && !request.getFeature().isEmpty()) {
                List<Predicate> features = new ArrayList<>();

                if (request.getFeature().contains(MatchFeature.AI_REPORT)) {
                    features.add(cb.isTrue(root.get("hasAiReport")));
                }

                if (request.getFeature().contains(MatchFeature.SCREEN)) {
                    features.add(cb.isTrue(root.get("hasScreen")));
                }

                predicates.add(cb.or(features.toArray(Predicate[]::new)));
            }

            // 마감 가리기와 임박 필터는 공통으로 '아직 신청 가능한 매치'만 대상으로 한다.
            Predicate available = cb.and(
                    cb.greaterThan(root.get("startTime"), now),
                    cb.lessThan(root.get("currentPlayers"), root.get("maxPlayers")),
                    cb.not(root.get("statusType").in(MatchStatus.CLOSED, MatchStatus.CANCELLED)));

            if (request.isHideClosed()) {
                predicates.add(available);
            }

            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                List<Predicate> states = new ArrayList<>();

                if (request.getStatus().contains(MatchStatus.CLOSING_SOON)) {
                    states.add(cb.lessThanOrEqualTo(
                            root.get("startTime"), now.plusHours(MatchAvailability.CLOSING_SOON_HOURS)));
                }

                if (request.getStatus().contains(MatchStatus.CONFIRMING_SOON)) {
                    states.add(cb.between(
                            cb.diff(root.<Integer>get("minPlayers"), root.<Integer>get("currentPlayers")),
                            1,
                            MatchAvailability.CONFIRMING_SOON_REMAINING));
                }

                // 두 임박 상태를 선택하면 어느 한 조건에 해당하는 매치를 반환한다.
                predicates.add(cb.and(available, cb.or(states.toArray(Predicate[]::new))));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static void in(List<Predicate> predicates, Path<?> path, List<?> values) {
        if (values != null && !values.isEmpty()) {
            predicates.add(path.in(values));
        }
    }

    private static void bool(List<Predicate> predicates, CriteriaBuilder cb, Path<Boolean> path, Boolean value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }

    private static void enabled(List<Predicate> predicates, CriteriaBuilder cb, Path<Boolean> path, Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            predicates.add(cb.isTrue(path));
        }
    }
}
