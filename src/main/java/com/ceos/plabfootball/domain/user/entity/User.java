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

    @Column(name = "guest_uuid", nullable = false, unique = true, length = 36)
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
}