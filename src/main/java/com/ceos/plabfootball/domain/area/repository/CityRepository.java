package com.ceos.plabfootball.domain.area.repository;

import com.ceos.plabfootball.domain.area.entity.City;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findAllByOrderByIdAsc();
}
