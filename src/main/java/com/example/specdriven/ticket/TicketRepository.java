package com.example.specdriven.ticket;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByShowId(Long showId);

    long countByShowId(Long showId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.show.id IN (SELECT s.id FROM Show s WHERE s.movie.id = :movieId AND s.dateTime > CURRENT_TIMESTAMP)")
    long countFutureTicketsByMovieId(@Param("movieId") Long movieId);

    boolean existsByShowIdAndSeatRowAndSeatNumber(Long showId, Integer seatRow, Integer seatNumber);
}
