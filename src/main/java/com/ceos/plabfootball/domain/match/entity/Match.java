package com.ceos.plabfootball.domain.match.entity;

import com.ceos.plabfootball.domain.stadium.entity.Stadium;
import com.ceos.plabfootball.domain.match.enums.*;
import com.ceos.plabfootball.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches", uniqueConstraints =
        {@UniqueConstraint(name = "uk_match_stadium_start_time",
                        columnNames = {"stadium_id", "start_time"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Match extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 구장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    // 매치 시작 및 종료 시간
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // 매치 가격
    @Column(name = "price", nullable = false)
    private Integer price;

    // 매치 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status_type", nullable = false, length = 20)
    private MatchStatus statusType;

    // 매치 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false, length = 20)
    private MatchType matchType;

    // 성별 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "gender_type", nullable = false, length = 20)
    private GenderType genderType;

    // 매치 레벨
    @Enumerated(EnumType.STRING)
    @Column(name = "level_type", nullable = false, length = 20)
    private LevelType levelType;

    // 최소 및 최대 플레이어 수
    @Column(name = "min_players", nullable = false)
    private Integer minPlayers;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    // 현재 신청 인원
    @Column(name = "current_players", nullable = false)
    private Integer currentPlayers;

    // 주차 가능 자리 수(매치에 할당된 총 주차 가능 자리)
    @Column(name = "parking_capacity")
    private Integer parkingCapacity;

    // 예약된 주차 자리 수
    @Column(name = "reserved_parking_count", nullable = false)
    private Integer reservedParkingCount;

    // 얼리버드 여부
    @Column(name = "is_earlybird")
    private Boolean isEarlybird;

    // AI 리포트 제공 여부
    @Column(name = "has_ai_report")
    private Boolean hasAiReport;

    // 스크린 제공 여부
    @Column(name = "has_screen")
    private Boolean hasScreen;
}