package com.example.specdriven.show;

import com.example.specdriven.movie.Movie;
import com.example.specdriven.movie.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UC-005: Manage Shows — business rules and data layer.
 */
@SpringBootTest
class ManageShowsTest {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningRoomRepository screeningRoomRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private Movie movie;
    private ScreeningRoom room;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        showRepository.deleteAll();
        movieRepository.deleteAll();
        screeningRoomRepository.deleteAll();

        movie = movieRepository.save(new Movie("Inception", "desc", 148, null));
        room = screeningRoomRepository.save(new ScreeningRoom("Room A", 5, 8));
    }

    @Test
    void canCreateShow() {
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));

        assertNotNull(show.getId());
        assertEquals("Inception", show.getMovie().getTitle());
        assertEquals("Room A", show.getScreeningRoom().getName());
    }

    @Test
    void canEditShowDateTimeAndRoom() {
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));
        ScreeningRoom newRoom = screeningRoomRepository.save(new ScreeningRoom("Room B", 3, 6));

        LocalDateTime newTime = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.MICROS);
        show.setDateTime(newTime);
        show.setScreeningRoom(newRoom);
        showRepository.save(show);

        Show updated = showRepository.findById(show.getId()).orElseThrow();
        assertEquals(newTime, updated.getDateTime());
        assertEquals("Room B", updated.getScreeningRoom().getName());
        // Movie unchanged
        assertEquals("Inception", updated.getMovie().getTitle());
    }

    @Test
    void canDeleteShowWithNoTickets() {
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));

        showRepository.delete(show);

        assertFalse(showRepository.findById(show.getId()).isPresent());
    }

    @Test
    void showWithSoldTicketsHasNonZeroTicketCount() {
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));
        ticketRepository.save(new Ticket(1, 1, "Alice", "alice@example.com", show));

        long count = ticketRepository.countByShowId(show.getId());
        assertEquals(1, count);
    }

    @Test
    void ticketCountZeroForShowWithNoTickets() {
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));

        assertEquals(0, ticketRepository.countByShowId(show.getId()));
    }

    @Test
    void findByScreeningRoomIdReturnsShowsInRoom() {
        showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));
        showRepository.save(new Show(LocalDateTime.now().plusDays(2), movie, room));

        ScreeningRoom otherRoom = screeningRoomRepository.save(new ScreeningRoom("Room B", 3, 6));
        showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, otherRoom));

        List<Show> roomShows = showRepository.findByScreeningRoomId(room.getId());
        assertEquals(2, roomShows.size());
    }

    // --- Overlap detection logic (tested as pure logic) ---

    @Test
    void overlappingShowsDetectedInSameRoom() {
        // Movie is 148 min + 30 min buffer = 178 min slot
        LocalDateTime show1Start = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        showRepository.save(new Show(show1Start, movie, room));

        // Show2 starts 2 hours later — overlaps since slot is ~3 hours
        LocalDateTime show2Start = show1Start.plusMinutes(120);

        List<Show> roomShows = showRepository.findByScreeningRoomId(room.getId());
        boolean overlaps = roomShows.stream().anyMatch(existing -> {
            int duration = existing.getMovie().getDurationMinutes() + 30;
            LocalDateTime existingEnd = existing.getDateTime().plusMinutes(duration);
            LocalDateTime newEnd = show2Start.plusMinutes(movie.getDurationMinutes() + 30);
            return show2Start.isBefore(existingEnd) && newEnd.isAfter(existing.getDateTime());
        });

        assertTrue(overlaps);
    }

    @Test
    void nonOverlappingShowsAllowedInSameRoom() {
        LocalDateTime show1Start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        showRepository.save(new Show(show1Start, movie, room));

        // Show2 starts 4 hours later — no overlap (148 + 30 = 178 min ≈ 3h)
        LocalDateTime show2Start = show1Start.plusMinutes(240);

        List<Show> roomShows = showRepository.findByScreeningRoomId(room.getId());
        boolean overlaps = roomShows.stream().anyMatch(existing -> {
            int duration = existing.getMovie().getDurationMinutes() + 30;
            LocalDateTime existingEnd = existing.getDateTime().plusMinutes(duration);
            LocalDateTime newEnd = show2Start.plusMinutes(movie.getDurationMinutes() + 30);
            return show2Start.isBefore(existingEnd) && newEnd.isAfter(existing.getDateTime());
        });

        assertFalse(overlaps);
    }

    @Test
    void showsInDifferentRoomsDoNotOverlap() {
        LocalDateTime time = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        showRepository.save(new Show(time, movie, room));

        ScreeningRoom otherRoom = screeningRoomRepository.save(new ScreeningRoom("Room B", 3, 6));
        List<Show> otherRoomShows = showRepository.findByScreeningRoomId(otherRoom.getId());

        assertTrue(otherRoomShows.isEmpty());
    }

    @Test
    void gridShowsTicketCounts() {
        Show show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));
        ticketRepository.save(new Ticket(1, 1, "Alice", "alice@example.com", show));
        ticketRepository.save(new Ticket(1, 2, "Bob", "bob@example.com", show));

        long sold = ticketRepository.countByShowId(show.getId());
        int total = show.getScreeningRoom().getRows() * show.getScreeningRoom().getSeatsPerRow();

        assertEquals(2, sold);
        assertEquals(40, total);
    }
}
