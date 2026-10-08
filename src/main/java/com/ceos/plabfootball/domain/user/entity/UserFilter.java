package com.ceos.plabfootball.domain.user.entity;

import com.ceos.plabfootball.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_filters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFilter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filter_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    @Column(name = "filter_setting", columnDefinition = "json")
    private String filterSetting;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;
}