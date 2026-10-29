package com.ceos.plabfootball.domain.favorite.repository;

import com.ceos.plabfootball.domain.area.entity.Area;
import com.ceos.plabfootball.domain.favorite.entity.FavoriteArea;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteAreaRepository extends JpaRepository<FavoriteArea, Long> {

    @Query("select f.area_id.id from FavoriteArea f "
            + "where f.user_id.id = :userId and f.area_id.city_id.id = :cityId")
    List<Long> findFavoriteAreaIds(@Param("userId") Long userId, @Param("cityId") Long cityId);

    @Query("select a from FavoriteArea f join f.area_id a join fetch a.city_id c "
            + "where f.user_id.id = :userId order by c.id, a.name, a.id")
    List<Area> findAreasForUser(@Param("userId") Long userId);

    // 사용자와 지역의 유니크 제약으로 중복 등록을 처리하고 기존 시각은 유지한다.
    // 네이티브 INSERT에는 JPA Auditing이 적용되지 않으므로 시각도 직접 저장한다.
    @Modifying(flushAutomatically = true)
    @Query(value = """
            INSERT INTO favorites_areas (user_id, areas_id, created_at, updated_at)
            VALUES (:userId, :areaId, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE id = id
            """, nativeQuery = true)
    void insertIfAbsent(@Param("userId") Long userId, @Param("areaId") Long areaId);

    @Modifying(flushAutomatically = true)
    @Query("delete from FavoriteArea f where f.user_id.id = :userId and f.area_id.id = :areaId")
    int deleteForUser(@Param("userId") Long userId, @Param("areaId") Long areaId);
}
