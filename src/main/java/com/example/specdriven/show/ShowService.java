package com.example.specdriven.show;

import com.example.specdriven.movie.Movie;
import com.example.specdriven.movie.MovieRepository;
import com.example.specdriven.room.ScreeningRoom;
import com.example.specdriven.room.ScreeningRoomRepository;
import com.example.specdriven.ticket.TicketRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreeningRoomRepository roomRepository;
    private final TicketRepository ticketRepository;

    public ShowService(ShowRepository showRepository, MovieRepository movieRepository,
            ScreeningRoomRepository roomRepository, TicketRepository ticketRepository) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<Show> findAll() {
        return showRepository.findAll();
    }

    public Show findById(Long id) {
        return showRepository.findById(id).orElse(null);
    }

    public List<Show> findFutureShowsForMovie(Long movieId) {
        return showRepository.findByMovieIdAndDateTimeAfterOrderByDateTime(movieId, LocalDateTime.now());
    }

    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    public List<ScreeningRoom> findAllRooms() {
        return roomRepository.findAll();
    }

    public long countTickets(Long showId) {
        return ticketRepository.countByShowId(showId);
    }

    public int totalCapacity(Show show) {
        ScreeningRoom room = show.getScreeningRoom();
        return room.getRows() * room.getSeatsPerRow();
    }

    @Transactional
    public Show save(Show show) {
        if (show.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Show must be scheduled in the future");
        }
        checkOverlap(show);
        return showRepository.save(show);
    }

    @Transactional
    public void delete(Long showId) {
        long tickets = ticketRepository.countByShowId(showId);
        if (tickets > 0) {
            throw new IllegalStateException("Cannot delete: show has sold tickets");
        }
        showRepository.deleteById(showId);
    }

    private void checkOverlap(Show show) {
        int duration = show.getMovie().getDurationMinutes() + 30; // 30 min buffer
        LocalDateTime start = show.getDateTime();
        LocalDateTime end = start.plusMinutes(duration);

        // Check if any other show in the same room overlaps
        // A show overlaps if its time block intersects with ours
        List<Show> roomShows = showRepository.findAll().stream()
                .filter(s -> s.getScreeningRoom().getId().equals(show.getScreeningRoom().getId()))
                .filter(s -> show.getId() == null || !s.getId().equals(show.getId()))
                .toList();

        for (Show existing : roomShows) {
            int existingDuration = existing.getMovie().getDurationMinutes() + 30;
            LocalDateTime existingStart = existing.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);

            if (start.isBefore(existingEnd) && end.isAfter(existingStart)) {
                throw new IllegalArgumentException(
                        "Time slot overlaps with \"" + existing.getMovie().getTitle()
                                + "\" at " + existing.getDateTime());
            }
        }
    }
}
