package com.ceos.plabfootball.domain.user.entity;

import com.ceos.plabfootball.domain.user.enums.UserLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "guest_uuid", nullable = false, unique = true, updatable = false,length = 36)
    private String guestUuid;

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 100)
    private UserLevel level;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 게스트 사용자 생성
    public static User createGuest() {
        User user = new User();

        user.guestUuid = UUID.randomUUID().toString();
        user.expiresAt = LocalDateTime.now().plusDays(7); //7일로 임시 설정

        return user;
    }

    // 게스트 인증 갱신 시 만료일 연장
    // 게스트 사용자가 7일 이내에 재방문하여 인증에 성공하면, 사용자 정보 보관 기간을 다시 7일 연장
    public void extendExpiration() {
        this.expiresAt = LocalDateTime.now().plusDays(7);
    }

    // 게스트 정보 만료 여부 확인
    public boolean isExpired() {
        return !expiresAt.isAfter(LocalDateTime.now());
    }
}