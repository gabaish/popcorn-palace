package com.att.tdp.popcorn_palace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.tdp.popcorn_palace.entities.Showtime;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    // check for overlapping showtimes in the same theater
    @Query("SELECT COUNT(s) FROM Showtime s WHERE s.theater = :theater AND (:excludeId IS NULL OR s.id <> :excludeId) AND NOT (:endTime <= s.startTime OR :startTime >= s.endTime)")
    long countOverlappingShowtimes(@Param("theater") String theater, @Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime, @Param("excludeId") Long excludeId);
}
