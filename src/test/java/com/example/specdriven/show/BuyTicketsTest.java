package com.example.specdriven.show;

import com.example.specdriven.movie.Movie;
import com.example.specdriven.movie.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UC-003: Select Seats & Buy Tickets.
 */
@SpringBootTest
class BuyTicketsTest {

    @Autowired
    private ShowEndpoint showEndpoint;

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
    private Show show;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        showRepository.deleteAll();
        movieRepository.deleteAll();
        screeningRoomRepository.deleteAll();

        room = screeningRoomRepository.save(new ScreeningRoom("Room A", 5, 8)); // 40 seats
        movie = movieRepository.save(
                new Movie("Inception", "A mind-bending thriller", 148, "inception.jpg"));
        show = showRepository.save(new Show(LocalDateTime.now().plusDays(1), movie, room));
    }

    // --- ShowDetail tests ---

    @Test
    void showDetailDisplaysMovieTitleDateTimeAndRoom() {
        ShowEndpoint.ShowDetail detail = showEndpoint.getShowDetail(show.getId());

        assertNotNull(detail);
        assertEquals("Inception", detail.movieTitle());
        assertEquals("Room A", detail.roomName());
        assertNotNull(detail.dateTime());
    }

    @Test
    void showDetailIncludesMovieId() {
        ShowEndpoint.ShowDetail detail = showEndpoint.getShowDetail(show.getId());

        assertNotNull(detail);
        assertEquals(movie.getId(), detail.movieId());
    }

    @Test
    void showDetailDisplaysSeatMapDimensions() {
        ShowEndpoint.ShowDetail detail = showEndpoint.getShowDetail(show.getId());

        assertEquals(5, detail.rows());
        assertEquals(8, detail.seatsPerRow());
    }

    @Test
    void showDetailListsSoldSeats() {
        ticketRepository.save(new Ticket(1, 1, "Alice", "alice@example.com", show));
        ticketRepository.save(new Ticket(2, 3, "Bob", "bob@example.com", show));

        ShowEndpoint.ShowDetail detail = showEndpoint.getShowDetail(show.getId());

        assertEquals(2, detail.soldSeats().size());
        assertTrue(detail.soldSeats().stream().anyMatch(s -> s.row() == 1 && s.seat() == 1));
        assertTrue(detail.soldSeats().stream().anyMatch(s -> s.row() == 2 && s.seat() == 3));
    }

    @Test
    void showDetailReturnsNullForNonExistentShow() {
        assertNull(showEndpoint.getShowDetail(99999L));
    }

    // --- Purchase validation tests ---

    @Test
    void purchaseFailsWithNoSeatsSelected() {
        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), List.of(), "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
        assertEquals("No seats selected", result.message());
    }

    @Test
    void purchaseFailsWithNullSeats() {
        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), null, "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
    }

    @Test
    void maximumSixSeatsPerTransaction() {
        List<ShowEndpoint.SeatRequest> seats = List.of(
                new ShowEndpoint.SeatRequest(1, 1),
                new ShowEndpoint.SeatRequest(1, 2),
                new ShowEndpoint.SeatRequest(1, 3),
                new ShowEndpoint.SeatRequest(1, 4),
                new ShowEndpoint.SeatRequest(1, 5),
                new ShowEndpoint.SeatRequest(1, 6),
                new ShowEndpoint.SeatRequest(1, 7)
        );

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
        assertEquals("Maximum 6 tickets per transaction", result.message());
    }

    @Test
    void exactlySixSeatsAllowed() {
        List<ShowEndpoint.SeatRequest> seats = List.of(
                new ShowEndpoint.SeatRequest(1, 1),
                new ShowEndpoint.SeatRequest(1, 2),
                new ShowEndpoint.SeatRequest(1, 3),
                new ShowEndpoint.SeatRequest(1, 4),
                new ShowEndpoint.SeatRequest(1, 5),
                new ShowEndpoint.SeatRequest(1, 6)
        );

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertTrue(result.success());
        assertEquals(6, result.tickets().size());
    }

    @Test
    void purchaseFailsWithBlankName() {
        List<ShowEndpoint.SeatRequest> seats = List.of(new ShowEndpoint.SeatRequest(1, 1));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "  ", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
        assertEquals("Name is required", result.message());
    }

    @Test
    void purchaseFailsWithNullName() {
        List<ShowEndpoint.SeatRequest> seats = List.of(new ShowEndpoint.SeatRequest(1, 1));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, null, "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
        assertEquals("Name is required", result.message());
    }

    @Test
    void purchaseFailsWithInvalidEmail() {
        List<ShowEndpoint.SeatRequest> seats = List.of(new ShowEndpoint.SeatRequest(1, 1));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "not-an-email");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
        assertEquals("Valid email is required", result.message());
    }

    @Test
    void purchaseFailsWithNullEmail() {
        List<ShowEndpoint.SeatRequest> seats = List.of(new ShowEndpoint.SeatRequest(1, 1));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", null);

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
        assertEquals("Valid email is required", result.message());
    }

    @Test
    void purchaseFailsForNonExistentShow() {
        List<ShowEndpoint.SeatRequest> seats = List.of(new ShowEndpoint.SeatRequest(1, 1));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                99999L, seats, "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
        assertEquals("Show not found", result.message());
    }

    // --- Successful purchase tests ---

    @Test
    void successfulPurchaseCreatesTickets() {
        List<ShowEndpoint.SeatRequest> seats = List.of(
                new ShowEndpoint.SeatRequest(1, 1),
                new ShowEndpoint.SeatRequest(2, 3)
        );

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertTrue(result.success());
        assertEquals(2, result.tickets().size());

        // Verify tickets exist in database
        List<Ticket> savedTickets = ticketRepository.findByShowId(show.getId());
        assertEquals(2, savedTickets.size());
    }

    @Test
    void successfulPurchaseReturnsConfirmationMessage() {
        List<ShowEndpoint.SeatRequest> seats = List.of(new ShowEndpoint.SeatRequest(1, 1));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertTrue(result.success());
        assertTrue(result.message().contains("1 ticket(s)"));
    }

    @Test
    void purchasedSeatsAppearAsSoldInShowDetail() {
        List<ShowEndpoint.SeatRequest> seats = List.of(
                new ShowEndpoint.SeatRequest(3, 5));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "alice@example.com");

        showEndpoint.purchaseTickets(request);

        ShowEndpoint.ShowDetail detail = showEndpoint.getShowDetail(show.getId());
        assertTrue(detail.soldSeats().stream().anyMatch(s -> s.row() == 3 && s.seat() == 5));
    }

    @Test
    void endpointIsAnnotatedAnonymousAllowed() {
        // AC: Page is accessible without authentication
        assertTrue(ShowEndpoint.class.isAnnotationPresent(
                com.vaadin.flow.server.auth.AnonymousAllowed.class));
    }

    @Test
    void purchaseConfirmationIncludesTicketPositions() {
        // BR-06: Confirmation includes details of purchased seats
        List<ShowEndpoint.SeatRequest> seats = List.of(
                new ShowEndpoint.SeatRequest(2, 4),
                new ShowEndpoint.SeatRequest(3, 5)
        );

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertTrue(result.success());
        assertEquals(2, result.tickets().size());
        assertTrue(result.tickets().stream().anyMatch(t -> t.row() == 2 && t.seat() == 4));
        assertTrue(result.tickets().stream().anyMatch(t -> t.row() == 3 && t.seat() == 5));
    }

    // --- Concurrent booking conflict test ---

    @Test
    void purchaseFailsWithBlankEmail() {
        List<ShowEndpoint.SeatRequest> seats = List.of(new ShowEndpoint.SeatRequest(1, 1));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "  ");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertFalse(result.success());
        assertEquals("Valid email is required", result.message());
    }

    @Test
    void singleSeatPurchaseAllowed() {
        // BR-01 lower boundary: 1 seat should be fine
        List<ShowEndpoint.SeatRequest> seats = List.of(new ShowEndpoint.SeatRequest(1, 1));

        ShowEndpoint.PurchaseRequest request = new ShowEndpoint.PurchaseRequest(
                show.getId(), seats, "Alice", "alice@example.com");

        ShowEndpoint.PurchaseResult result = showEndpoint.purchaseTickets(request);

        assertTrue(result.success());
        assertEquals(1, result.tickets().size());
    }

    @Test
    void duplicateSeatForSameShowRejectedByUniqueConstraint() {
        // The unique constraint on (show_id, seat_row, seat_number) prevents double-booking
        ticketRepository.saveAndFlush(new Ticket(1, 1, "Bob", "bob@example.com", show));

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () ->
                ticketRepository.saveAndFlush(new Ticket(1, 1, "Alice", "alice@example.com", show)));
    }
}
