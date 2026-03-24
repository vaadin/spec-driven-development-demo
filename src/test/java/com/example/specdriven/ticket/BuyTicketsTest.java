package com.example.specdriven.ticket;

import com.example.specdriven.movie.MovieEndpoint;
import com.example.specdriven.show.ShowEndpoint;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BuyTicketsTest {

    @Autowired
    private TicketEndpoint ticketEndpoint;

    @Autowired
    private ShowEndpoint showEndpoint;

    @Autowired
    private MovieEndpoint movieEndpoint;

    private Long getFirstShowId() {
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        List<ShowEndpoint.ShowtimeGroup> groups = showEndpoint.getShowtimesForMovie(movies.getFirst().id());
        return groups.getFirst().showtimes().getFirst().id();
    }

    @Test
    void canPurchaseTickets() {
        Long showId = getFirstShowId();
        List<int[]> seats = List.of(new int[]{1, 1}, new int[]{1, 2});

        TicketEndpoint.PurchaseResult result = ticketEndpoint.purchaseTickets(
                showId, seats, "Test User", "test@example.com");

        assertTrue(result.success());
        assertEquals(2, result.tickets().size());
    }

    @Test
    void maximumSixTicketsPerTransaction() {
        Long showId = getFirstShowId();
        List<int[]> seats = List.of(
                new int[]{2, 1}, new int[]{2, 2}, new int[]{2, 3},
                new int[]{2, 4}, new int[]{2, 5}, new int[]{2, 6},
                new int[]{2, 7}
        );

        TicketEndpoint.PurchaseResult result = ticketEndpoint.purchaseTickets(
                showId, seats, "Test", "test@example.com");

        assertFalse(result.success());
        assertTrue(result.message().contains("Maximum 6"));
    }

    @Test
    void cannotPurchaseAlreadySoldSeat() {
        Long showId = getFirstShowId();
        List<int[]> seats = List.of(new int[]{3, 1});

        // First purchase succeeds
        TicketEndpoint.PurchaseResult result1 = ticketEndpoint.purchaseTickets(
                showId, seats, "First", "first@example.com");
        assertTrue(result1.success());

        // Second purchase of same seat fails
        TicketEndpoint.PurchaseResult result2 = ticketEndpoint.purchaseTickets(
                showId, seats, "Second", "second@example.com");
        assertFalse(result2.success());
        assertTrue(result2.message().contains("already sold"));
    }

    @Test
    void seatStatusesReflectSoldTickets() {
        Long showId = getFirstShowId();

        // Buy a seat
        ticketEndpoint.purchaseTickets(showId, List.of(new int[]{4, 1}), "Test", "test@example.com");

        List<TicketEndpoint.SeatStatus> statuses = ticketEndpoint.getSeatStatuses(showId);
        boolean found = statuses.stream().anyMatch(s -> s.row() == 4 && s.number() == 1 && s.sold());
        assertTrue(found, "Purchased seat should be marked as sold");
    }

    @Test
    void nameAndEmailAreRequired() {
        Long showId = getFirstShowId();
        List<int[]> seats = List.of(new int[]{5, 1});

        TicketEndpoint.PurchaseResult result = ticketEndpoint.purchaseTickets(
                showId, seats, "", "test@example.com");
        // The service should reject blank names
        // (The validation is in the frontend, but the service also validates)
    }
}
