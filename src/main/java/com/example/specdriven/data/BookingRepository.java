package com.example.specdriven.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    long countByScreening(Screening screening);

    @Query("SELECT b FROM Booking b JOIN FETCH b.screening s JOIN FETCH s.movie ORDER BY b.id DESC")
    List<Booking> findAllWithDetails();

    @Query("SELECT b FROM Booking b JOIN FETCH b.screening s JOIN FETCH s.movie " +
           "WHERE LOWER(b.confirmationCode) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(b.customerName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(b.customerEmail) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "ORDER BY b.id DESC")
    List<Booking> searchByTerm(@Param("term") String term);
}
