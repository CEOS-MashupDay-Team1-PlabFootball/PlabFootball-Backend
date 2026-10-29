package com.ceos.plabfootball.domain.user.repository;

import com.ceos.plabfootball.domain.user.entity.UserFilter;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFilterRepository extends JpaRepository<UserFilter, Long> {
    @Query("select f from UserFilter f where f.user_id.id = :userId order by f.updatedAt desc, f.id desc")
    List<UserFilter> findAllForUser(@Param("userId") Long userId);

    @Query("select f from UserFilter f where f.id = :presetId and f.user_id.id = :userId")
    Optional<UserFilter> findForUser(@Param("presetId") Long presetId, @Param("userId") Long userId);
}
