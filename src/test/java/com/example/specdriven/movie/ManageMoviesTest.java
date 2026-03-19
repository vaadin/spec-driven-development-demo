package com.example.specdriven.movie;

import com.example.specdriven.show.ScreeningRoom;
import com.example.specdriven.show.ScreeningRoomRepository;
import com.example.specdriven.show.Show;
import com.example.specdriven.show.ShowRepository;
import com.example.specdriven.show.Ticket;
import com.example.specdriven.show.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UC-004: Manage Movies (Admin) — business rules and data layer.
 */
@SpringBootTest
class ManageMoviesTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ScreeningRoomRepository screeningRoomRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private ScreeningRoom room;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        showRepository.deleteAll();
        movieRepository.deleteAll();
        screeningRoomRepository.deleteAll();

        room = screeningRoomRepository.save(new ScreeningRoom("Room A", 5, 8));
    }

    // --- List movies ---

    @Test
    void allMoviesListedRegardlessOfShows() {
        movieRepository.save(new Movie("Inception", "desc", 148, null));
        movieRepository.save(new Movie("Dune", "desc", 155, null));

        List<Movie> all = movieRepository.findAll();
        assertEquals(2, all.size());
    }

    // --- Add movie ---

    @Test
    void canAddNewMovie() {
        Movie movie = movieRepository.save(new Movie("New Movie", "A new film", 120, "poster.jpg"));

        assertNotNull(movie.getId());
        assertEquals("New Movie", movie.getTitle());
        assertEquals("A new film", movie.getDescription());
        assertEquals(120, movie.getDurationMinutes());
        assertEquals("poster.jpg", movie.getPosterFileName());
    }

    // --- Edit movie ---

    @Test
    void canEditExistingMovie() {
        Movie movie = movieRepository.save(new Movie("Old Title", "desc", 100, null));

        movie.setTitle("New Title");
        movie.setDurationMinutes(110);
        movieRepository.save(movie);

        Movie updated = movieRepository.findById(movie.getId()).orElseThrow();
        assertEquals("New Title", updated.getTitle());
        assertEquals(110, updated.getDurationMinutes());
    }

    // --- BR-01: Title is required and must be unique ---

    @Test
    void titleMustBeUnique() {
        movieRepository.save(new Movie("Inception", "desc", 148, null));

        assertTrue(movieRepository.existsByTitle("Inception"));
        assertFalse(movieRepository.existsByTitle("Dune"));
    }

    @Test
    void titleUniqueCheckExcludesCurrentMovie() {
        Movie movie = movieRepository.save(new Movie("Inception", "desc", 148, null));

        // Same title, same movie — should be allowed (editing without changing title)
        assertFalse(movieRepository.existsByTitleAndIdNot("Inception", movie.getId()));
        // Same title, different movie — should be rejected
        Movie other = movieRepository.save(new Movie("Dune", "desc", 155, null));
        assertTrue(movieRepository.existsByTitleAndIdNot("Inception", other.getId()));
    }

    @Test
    void uniqueConstraintEnforcedAtDatabaseLevel() {
        movieRepository.saveAndFlush(new Movie("Inception", "desc1", 148, null));

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () ->
                movieRepository.saveAndFlush(new Movie("Inception", "desc2", 150, null)));
    }

    // --- BR-03: Cannot delete movie with future shows that have sold tickets ---

    @Test
    void canDeleteMovieWithNoShows() {
        Movie movie = movieRepository.save(new Movie("Inception", "desc", 148, null));
        movieRepository.delete(movie);

        assertFalse(movieRepository.findById(movie.getId()).isPresent());
    }

    @Test
    void canDeleteMovieWithFutureShowsButNoTickets() {
        Movie movie = movieRepository.save(new Movie("Inception", "desc", 148, null));
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));

        // No tickets sold — delete the show first, then movie
        showRepository.delete(show);
        movieRepository.delete(movie);

        assertFalse(movieRepository.findById(movie.getId()).isPresent());
    }

    @Test
    void canDeleteMovieWithPastShowsAndTickets() {
        Movie movie = movieRepository.save(new Movie("Inception", "desc", 148, null));
        Show pastShow = showRepository.save(new Show(LocalDateTime.now().minusDays(1), movie, room));
        ticketRepository.save(new Ticket(1, 1, "Alice", "alice@example.com", pastShow));

        // Past shows with tickets — should be deletable
        // (business rule only protects future shows with tickets)
        boolean hasFutureSoldTickets = showRepository
                .findByMovieIdAndDateTimeAfterOrderByDateTimeAsc(movie.getId(), LocalDateTime.now())
                .stream()
                .anyMatch(show -> ticketRepository.countByShowId(show.getId()) > 0);

        assertFalse(hasFutureSoldTickets);
    }

    @Test
    void cannotDeleteMovieWithFutureShowsAndSoldTickets() {
        Movie movie = movieRepository.save(new Movie("Inception", "desc", 148, null));
        Show futureShow = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));
        ticketRepository.save(new Ticket(1, 1, "Alice", "alice@example.com", futureShow));

        boolean hasFutureSoldTickets = showRepository
                .findByMovieIdAndDateTimeAfterOrderByDateTimeAsc(movie.getId(), LocalDateTime.now())
                .stream()
                .anyMatch(show -> ticketRepository.countByShowId(show.getId()) > 0);

        assertTrue(hasFutureSoldTickets);
    }

    // --- Show count ---

    @Test
    void showCountReflectsScheduledShows() {
        Movie movie = movieRepository.save(new Movie("Inception", "desc", 148, null));
        showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));
        showRepository.save(new Show(LocalDateTime.now().plusDays(2), movie, room));

        assertEquals(2, showRepository.countByMovieId(movie.getId()));
    }
}
