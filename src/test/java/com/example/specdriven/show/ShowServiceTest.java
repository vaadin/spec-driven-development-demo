package com.example.specdriven.show;

import com.example.specdriven.movie.Movie;
import com.example.specdriven.movie.MovieRepository;
import com.example.specdriven.room.ScreeningRoom;
import com.example.specdriven.room.ScreeningRoomRepository;
import com.example.specdriven.ticket.Ticket;
import com.example.specdriven.ticket.TicketRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShowServiceTest {

    @Autowired
    private ShowService showService;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningRoomRepository roomRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private Movie movie;
    private ScreeningRoom room;

    @BeforeEach
    void setup() {
        movie = new Movie();
        movie.setTitle("Show Test Movie " + System.nanoTime());
        movie.setDurationMinutes(90);
        movie = movieRepository.save(movie);

        room = new ScreeningRoom();
        room.setName("Show Test Room " + System.nanoTime());
        room.setRows(5);
        room.setSeatsPerRow(5);
        room = roomRepository.save(room);
    }

    @Test
    void canScheduleFutureShow() {
        Show show = new Show();
        show.setMovie(movie);
        show.setScreeningRoom(room);
        show.setDateTime(LocalDateTime.now().plusDays(10));

        Show saved = showService.save(show);
        assertNotNull(saved.getId());
    }

    @Test
    void pastShowIsRejected() {
        Show show = new Show();
        show.setMovie(movie);
        show.setScreeningRoom(room);
        show.setDateTime(LocalDateTime.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> showService.save(show));
    }

    @Test
    void overlappingShowInSameRoomIsRejected() {
        LocalDateTime time = LocalDateTime.now().plusDays(20);

        Show show1 = new Show();
        show1.setMovie(movie);
        show1.setScreeningRoom(room);
        show1.setDateTime(time);
        showService.save(show1);

        Show show2 = new Show();
        show2.setMovie(movie);
        show2.setScreeningRoom(room);
        show2.setDateTime(time.plusMinutes(30)); // overlaps with show1 (90min + 30min buffer)

        assertThrows(IllegalArgumentException.class, () -> showService.save(show2));
    }

    @Test
    void canDeleteShowWithNoTickets() {
        Show show = new Show();
        show.setMovie(movie);
        show.setScreeningRoom(room);
        show.setDateTime(LocalDateTime.now().plusDays(15));
        show = showService.save(show);

        Long id = show.getId();
        showService.delete(id);
        assertNull(showService.findById(id));
    }

    @Test
    void cannotDeleteShowWithSoldTickets() {
        Show show = new Show();
        show.setMovie(movie);
        show.setScreeningRoom(room);
        show.setDateTime(LocalDateTime.now().plusDays(15));
        show = showRepository.save(show);

        Ticket ticket = new Ticket();
        ticket.setShow(show);
        ticket.setSeatRow(1);
        ticket.setSeatNumber(1);
        ticket.setCustomerName("Test");
        ticket.setCustomerEmail("test@test.com");
        ticket.setPurchasedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        Long showId = show.getId();
        assertThrows(IllegalStateException.class, () -> showService.delete(showId));
    }
}
