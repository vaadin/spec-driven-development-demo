package com.example.specdriven.show;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieIdAndDateTimeAfterOrderByDateTime(Long movieId, LocalDateTime after);

    @Query("SELECT COUNT(s) FROM Show s WHERE s.movie.id = :movieId")
    long countByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT s FROM Show s WHERE s.screeningRoom.id = :roomId AND s.dateTime BETWEEN :start AND :end")
    List<Show> findOverlapping(@Param("roomId") Long roomId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Show s WHERE s.movie.id = :movieId AND s.dateTime > CURRENT_TIMESTAMP")
    long countFutureShowsByMovieId(@Param("movieId") Long movieId);
}
