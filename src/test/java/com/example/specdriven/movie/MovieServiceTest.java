package com.example.specdriven.movie;

import com.example.specdriven.room.ScreeningRoom;
import com.example.specdriven.room.ScreeningRoomRepository;
import com.example.specdriven.show.Show;
import com.example.specdriven.show.ShowRepository;
import com.example.specdriven.ticket.Ticket;
import com.example.specdriven.ticket.TicketRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ScreeningRoomRepository roomRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void canListAllMovies() {
        var movies = movieService.findAll();
        assertFalse(movies.isEmpty());
    }

    @Test
    void canSaveNewMovie() {
        Movie movie = new Movie();
        movie.setTitle("Test Movie " + System.nanoTime());
        movie.setDescription("A test movie");
        movie.setDurationMinutes(120);

        Movie saved = movieService.save(movie);
        assertNotNull(saved.getId());
        assertEquals(movie.getTitle(), saved.getTitle());
    }

    @Test
    void duplicateTitleIsRejected() {
        Movie movie1 = new Movie();
        movie1.setTitle("Duplicate Title Test " + System.nanoTime());
        movie1.setDurationMinutes(90);
        movieService.save(movie1);

        Movie movie2 = new Movie();
        movie2.setTitle(movie1.getTitle());
        movie2.setDurationMinutes(100);

        assertThrows(IllegalArgumentException.class, () -> movieService.save(movie2));
    }

    @Test
    void canEditExistingMovie() {
        Movie movie = new Movie();
        movie.setTitle("Edit Test " + System.nanoTime());
        movie.setDurationMinutes(90);
        movie = movieService.save(movie);

        movie.setDurationMinutes(120);
        Movie updated = movieService.save(movie);
        assertEquals(120, updated.getDurationMinutes());
    }

    @Test
    void canDeleteMovieWithoutFutureSoldTickets() {
        Movie movie = new Movie();
        movie.setTitle("Delete Test " + System.nanoTime());
        movie.setDurationMinutes(90);
        movie = movieService.save(movie);

        Long id = movie.getId();
        movieService.delete(id);
        assertNull(movieService.findById(id));
    }

    @Test
    void cannotDeleteMovieWithFutureSoldTickets() {
        Movie movie = new Movie();
        movie.setTitle("No Delete Test " + System.nanoTime());
        movie.setDurationMinutes(90);
        movie = movieRepository.save(movie);

        ScreeningRoom room = new ScreeningRoom();
        room.setName("Test Room " + System.nanoTime());
        room.setRows(5);
        room.setSeatsPerRow(5);
        room = roomRepository.save(room);

        Show show = new Show();
        show.setMovie(movie);
        show.setScreeningRoom(room);
        show.setDateTime(LocalDateTime.now().plusDays(7));
        show = showRepository.save(show);

        Ticket ticket = new Ticket();
        ticket.setShow(show);
        ticket.setSeatRow(1);
        ticket.setSeatNumber(1);
        ticket.setCustomerName("Test");
        ticket.setCustomerEmail("test@test.com");
        ticket.setPurchasedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        Long movieId = movie.getId();
        assertThrows(IllegalStateException.class, () -> movieService.delete(movieId));
    }
}
