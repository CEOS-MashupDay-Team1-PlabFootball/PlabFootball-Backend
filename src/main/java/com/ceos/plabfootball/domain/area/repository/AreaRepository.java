package com.ceos.plabfootball.domain.area.repository;

import com.ceos.plabfootball.domain.area.entity.Area;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AreaRepository extends JpaRepository<Area, Long> {
    @Query("select a from Area a join fetch a.city_id c where c.id = :cityId order by a.name, a.id")
    List<Area> findAllByCityId(@Param("cityId") Long cityId);
}
