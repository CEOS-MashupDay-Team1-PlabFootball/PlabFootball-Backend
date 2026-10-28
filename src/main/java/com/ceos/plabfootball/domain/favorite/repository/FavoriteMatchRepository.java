package com.ceos.plabfootball.domain.favorite.repository;

import com.ceos.plabfootball.domain.favorite.entity.FavoriteMatch;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface FavoriteMatchRepository extends JpaRepository<FavoriteMatch, Long> {
    interface FavoriteCount {
        Long getMatchId();
        Long getFavoriteCount();
    }

    @Query("select f.match_id.id as matchId, count(f) as favoriteCount from FavoriteMatch f "
            + "where f.match_id.id in :ids group by f.match_id.id")
    List<FavoriteCount> countByMatchIds(@Param("ids") Collection<Long> ids);

    @Query("select f.match_id.id from FavoriteMatch f where f.user_id.id = :userId and f.match_id.id in :ids")
    List<Long> findFavoriteMatchIds(@Param("userId") Long userId, @Param("ids") Collection<Long> ids);

    // 유니크 제약으로 중복을 처리하며, 재요청 시 기존 ID와 생성 및 수정 시각을 유지한다.
    // 네이티브 INSERT에는 JPA Auditing이 적용되지 않으므로 시각도 함께 저장한다.
    @Modifying(flushAutomatically = true)
    @Query(value = """
            INSERT INTO favorites_matches (user_id, matches_id, created_at, updated_at)
            VALUES (:userId, :matchId, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE id = id
            """, nativeQuery = true)
    void insertIfAbsent(@Param("userId") Long userId, @Param("matchId") Long matchId);

    @Query("select count(f) from FavoriteMatch f where f.match_id.id = :matchId")
    long countForMatch(@Param("matchId") Long matchId);

    @Modifying(flushAutomatically = true)
    @Query("delete from FavoriteMatch f where f.user_id.id = :userId and f.match_id.id = :matchId")
    int deleteForUser(@Param("userId") Long userId, @Param("matchId") Long matchId);
}
