package com.ceos.plabfootball.domain.match.repository;

import com.ceos.plabfootball.domain.match.entity.Match;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long>, JpaSpecificationExecutor<Match> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Match m where m.id = :id")
    Optional<Match> findForUpdate(@Param("id") Long id);
}
