package com.ceos.plabfootball.domain.stadium.entity;

import com.ceos.plabfootball.domain.area.entity.Area;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stadium_groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StadiumGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "areas_id", nullable = false)
    private Area area;
}