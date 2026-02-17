package com.example.specdriven.data;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("SELECT s FROM Screening s JOIN FETCH s.movie WHERE s.startTime >= :dayStart AND s.startTime < :dayEnd ORDER BY s.startTime")
    List<Screening> findScreeningsByDate(@Param("dayStart") LocalDateTime dayStart,
                                         @Param("dayEnd") LocalDateTime dayEnd);

    long countByMovieAndStartTimeAfter(Movie movie, LocalDateTime dateTime);
}
