package com.ceos.plabfootball.domain.favorite.entity;

import com.ceos.plabfootball.domain.match.entity.Match;
import com.ceos.plabfootball.domain.user.entity.User;
import com.ceos.plabfootball.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorites_matches", uniqueConstraints =
        {@UniqueConstraint(name = "uk_favorite_match_user_match",
                columnNames = {"user_id", "matches_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteMatch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 찜한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 찜한 매치
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matches_id", nullable = false)
    private Match match;
}