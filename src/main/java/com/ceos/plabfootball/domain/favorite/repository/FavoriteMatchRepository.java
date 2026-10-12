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

    @Query("select count(f) > 0 from FavoriteMatch f where f.user_id.id = :userId and f.match_id.id = :matchId")
    boolean existsForUser(@Param("userId") Long userId, @Param("matchId") Long matchId);

    @Query("select count(f) from FavoriteMatch f where f.match_id.id = :matchId")
    long countForMatch(@Param("matchId") Long matchId);

    @Modifying(flushAutomatically = true)
    @Query("delete from FavoriteMatch f where f.user_id.id = :userId and f.match_id.id = :matchId")
    int deleteForUser(@Param("userId") Long userId, @Param("matchId") Long matchId);
}
