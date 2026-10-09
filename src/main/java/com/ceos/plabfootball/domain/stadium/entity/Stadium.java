package com.ceos.plabfootball.domain.stadium.entity;

import com.ceos.plabfootball.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "stadiums")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stadium extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 구장 그룹
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_groups_id", nullable = false)
    private StadiumGroup stadiumGroup;

    // 구장 이름
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    // 구장 주소
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    // 위도
    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    // 경도
    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    // 구장 이미지 URL
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // 실내 여부
    @Column(name = "is_indoor")
    private Boolean isIndoor;

    // 그늘막 여부
    @Column(name = "has_awning")
    private Boolean hasAwning;

    // 주차 가능 여부
    @Column(name = "has_parking")
    private Boolean hasParking;

    // 신발 대여 여부
    @Column(name = "has_shoe_rental")
    private Boolean hasShoeRental;

    // 구장 시설 정보
    @Column(name = "facility_info", columnDefinition = "TEXT")
    private String facilityInfo;
}