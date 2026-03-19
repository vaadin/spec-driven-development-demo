package com.example.specdriven.show;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieIdAndDateTimeAfterOrderByDateTimeAsc(Long movieId, LocalDateTime now);

    long countByMovieId(Long movieId);

    List<Show> findByMovieId(Long movieId);

    List<Show> findByScreeningRoomId(Long screeningRoomId);
}
