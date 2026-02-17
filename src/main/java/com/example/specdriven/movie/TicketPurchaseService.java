package com.example.specdriven.movie;

import com.example.specdriven.data.Booking;
import com.example.specdriven.data.BookingRepository;
import com.example.specdriven.data.Movie;
import com.example.specdriven.data.MovieRepository;
import com.example.specdriven.data.Screening;
import com.example.specdriven.data.ScreeningRepository;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@BrowserCallable
@AnonymousAllowed
public class TicketPurchaseService {

    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;

    public TicketPurchaseService(ScreeningRepository screeningRepository,
                                 BookingRepository bookingRepository,
                                 MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.bookingRepository = bookingRepository;
        this.movieRepository = movieRepository;
    }

    public MovieDetailsDTO getMovieDetails(long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Screening> screenings = screeningRepository.findScreeningsByDate(dayStart, dayEnd);
        List<ScreeningDetailDTO> screeningDTOs = new ArrayList<>();
        for (Screening s : screenings) {
            if (s.getMovie().getId().equals(movie.getId())) {
                screeningDTOs.add(new ScreeningDetailDTO(
                        s.getId(),
                        s.getStartTime().toLocalTime(),
                        s.getAvailableSeats(),
                        s.getStartTime().isBefore(now)));
            }
        }

        return new MovieDetailsDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getPosterFileName(),
                movie.getDurationMinutes(),
                screeningDTOs);
    }

    @Transactional
    public PurchaseConfirmationDTO purchaseTickets(long screeningId, int ticketCount,
                                                    String customerName, String customerEmail) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (ticketCount < 1 || ticketCount > 10) {
            throw new IllegalArgumentException("Ticket count must be between 1 and 10");
        }

        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("Screening not found"));

        if (screening.getAvailableSeats() < ticketCount) {
            throw new IllegalArgumentException("Not enough seats available. Only "
                    + screening.getAvailableSeats() + " seats left.");
        }

        screening.setAvailableSeats(screening.getAvailableSeats() - ticketCount);
        screeningRepository.save(screening);

        String confirmationCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = new Booking(confirmationCode, customerName, customerEmail,
                screening, ticketCount);
        bookingRepository.save(booking);

        return new PurchaseConfirmationDTO(
                confirmationCode,
                screening.getMovie().getTitle(),
                screening.getStartTime().toLocalTime(),
                ticketCount);
    }

    public record MovieDetailsDTO(
            long id,
            String title,
            String description,
            String posterFileName,
            int durationMinutes,
            List<ScreeningDetailDTO> screenings) {
    }

    public record ScreeningDetailDTO(
            long id,
            LocalTime time,
            int availableSeats,
            boolean past) {
    }

    public record PurchaseConfirmationDTO(
            String confirmationCode,
            String movieTitle,
            LocalTime showTime,
            int ticketCount) {
    }
}
