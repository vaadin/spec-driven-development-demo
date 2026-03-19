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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UC-002: View Showtimes & Select Show.
 */
@SpringBootTest
class ViewShowtimesTest {

    @Autowired
    private MovieEndpoint movieEndpoint;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ScreeningRoomRepository screeningRoomRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private ScreeningRoom room;
    private Movie movie;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        showRepository.deleteAll();
        movieRepository.deleteAll();
        screeningRoomRepository.deleteAll();

        room = screeningRoomRepository.save(new ScreeningRoom("Room A", 5, 8)); // 40 seats
        movie = movieRepository.save(
                new Movie("Inception", "A mind-bending thriller", 148, "inception.jpg"));
    }

    @Test
    void movieDetailShowsPosterTitleDescriptionDuration() {
        showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));

        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieDetail(movie.getId());

        assertNotNull(detail);
        assertEquals("Inception", detail.title());
        assertEquals("A mind-bending thriller", detail.description());
        assertEquals(148, detail.durationMinutes());
        assertEquals("inception.jpg", detail.posterFileName());
    }

    @Test
    void onlyFutureShowtimesDisplayed() {
        showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));
        showRepository.save(new Show(LocalDateTime.now().minusDays(1), movie, room));

        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieDetail(movie.getId());

        assertEquals(1, detail.shows().size());
        assertTrue(LocalDateTime.parse(detail.shows().get(0).dateTime()).isAfter(LocalDateTime.now()));
    }

    @Test
    void showtimesSortedChronologically() {
        LocalDateTime laterShow = LocalDateTime.now().plusDays(3);
        LocalDateTime earlierShow = LocalDateTime.now().plusDays(1);

        showRepository.save(new Show(laterShow, movie, room));
        showRepository.save(new Show(earlierShow, movie, room));

        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieDetail(movie.getId());

        assertEquals(2, detail.shows().size());
        LocalDateTime first = LocalDateTime.parse(detail.shows().get(0).dateTime());
        LocalDateTime second = LocalDateTime.parse(detail.shows().get(1).dateTime());
        assertTrue(first.isBefore(second));
    }

    @Test
    void eachShowtimeDisplaysTimeRoomAndAvailableSeats() {
        showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));

        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieDetail(movie.getId());

        MovieEndpoint.ShowInfo showInfo = detail.shows().get(0);
        assertNotNull(showInfo.dateTime());
        assertEquals("Room A", showInfo.roomName());
        assertEquals(40, showInfo.totalSeats());
        assertEquals(40, showInfo.availableSeats());
        assertFalse(showInfo.soldOut());
    }

    @Test
    void availableSeatsEqualsTotalMinusSoldTickets() {
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));

        ticketRepository.save(new Ticket(1, 1, "Alice", "alice@example.com", show));
        ticketRepository.save(new Ticket(1, 2, "Bob", "bob@example.com", show));
        ticketRepository.save(new Ticket(1, 3, "Carol", "carol@example.com", show));

        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieDetail(movie.getId());

        MovieEndpoint.ShowInfo showInfo = detail.shows().get(0);
        assertEquals(40, showInfo.totalSeats());
        assertEquals(37, showInfo.availableSeats());
        assertFalse(showInfo.soldOut());
    }

    @Test
    void soldOutShowMarkedAsSoldOut() {
        ScreeningRoom tinyRoom = screeningRoomRepository.save(new ScreeningRoom("Tiny", 1, 2)); // 2 seats
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, tinyRoom));

        ticketRepository.save(new Ticket(1, 1, "Alice", "alice@example.com", show));
        ticketRepository.save(new Ticket(1, 2, "Bob", "bob@example.com", show));

        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieDetail(movie.getId());

        MovieEndpoint.ShowInfo showInfo = detail.shows().get(0);
        assertEquals(0, showInfo.availableSeats());
        assertTrue(showInfo.soldOut());
    }

    @Test
    void nonExistentMovieReturnsNull() {
        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieDetail(99999L);

        assertNull(detail);
    }

    @Test
    void movieWithNoFutureShowsReturnsEmptyShowList() {
        showRepository.save(new Show(LocalDateTime.now().minusDays(1), movie, room));

        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieDetail(movie.getId());

        assertNotNull(detail);
        assertEquals("Inception", detail.title());
        assertTrue(detail.shows().isEmpty());
    }
}
