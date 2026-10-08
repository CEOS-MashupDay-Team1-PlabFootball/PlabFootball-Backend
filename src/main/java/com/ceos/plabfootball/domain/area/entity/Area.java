package com.ceos.plabfootball.domain.area.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "areas",uniqueConstraints =
        {@UniqueConstraint(name = "uk_area_city_name",
                                            columnNames = {"cities_id", "name"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cities_id", nullable = false)
    private City city;

    @Column(name = "name", nullable = false, length = 255)
    private String name;
}