package com.example.specdriven.movie;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT DISTINCT m FROM Movie m JOIN Show s ON s.movie = m " +
           "WHERE s.dateTime > :now ORDER BY m.title")
    List<Movie> findMoviesWithFutureShows(LocalDateTime now);
}
