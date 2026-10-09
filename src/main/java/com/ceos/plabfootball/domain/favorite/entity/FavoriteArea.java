package com.ceos.plabfootball.domain.favorite.entity;

import com.ceos.plabfootball.domain.area.entity.Area;
import com.ceos.plabfootball.domain.user.entity.User;
import com.ceos.plabfootball.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorites_areas", uniqueConstraints =
        {@UniqueConstraint(name = "uk_favorite_area_user_area",
                        columnNames = {"user_id", "areas_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteArea extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 찜한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 찜한 지역
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "areas_id", nullable = false)
    private Area area;
}