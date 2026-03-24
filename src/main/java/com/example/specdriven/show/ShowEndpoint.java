package com.example.specdriven.show;

import com.example.specdriven.ticket.TicketRepository;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@BrowserCallable
@AnonymousAllowed
public class ShowEndpoint {

    private final ShowService showService;
    private final TicketRepository ticketRepository;

    public ShowEndpoint(ShowService showService, TicketRepository ticketRepository) {
        this.showService = showService;
        this.ticketRepository = ticketRepository;
    }

    public record ShowtimeInfo(Long id, String time, String roomName, int availableSeats, int totalSeats, boolean soldOut) {}

    public record ShowtimeGroup(String date, List<ShowtimeInfo> showtimes) {}

    public record ShowDetail(Long id, String movieTitle, Long movieId, String dateTime, String roomName,
            int rows, int seatsPerRow) {}

    public List<ShowtimeGroup> getShowtimesForMovie(Long movieId) {
        List<Show> shows = showService.findFutureShowsForMovie(movieId);
        Map<LocalDate, List<Show>> grouped = new LinkedHashMap<>();
        for (Show show : shows) {
            grouped.computeIfAbsent(show.getDateTime().toLocalDate(), k -> new java.util.ArrayList<>()).add(show);
        }

        return grouped.entrySet().stream().map(entry -> {
            String dateStr = entry.getKey().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"));
            List<ShowtimeInfo> infos = entry.getValue().stream().map(show -> {
                int total = showService.totalCapacity(show);
                long sold = showService.countTickets(show.getId());
                int available = total - (int) sold;
                String time = show.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                return new ShowtimeInfo(show.getId(), time, show.getScreeningRoom().getName(),
                        available, total, available == 0);
            }).toList();
            return new ShowtimeGroup(dateStr, infos);
        }).toList();
    }

    public ShowDetail getShowDetail(Long showId) {
        Show show = showService.findById(showId);
        if (show == null) return null;
        return new ShowDetail(
                show.getId(),
                show.getMovie().getTitle(),
                show.getMovie().getId(),
                show.getDateTime().format(DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' HH:mm")),
                show.getScreeningRoom().getName(),
                show.getScreeningRoom().getRows(),
                show.getScreeningRoom().getSeatsPerRow()
        );
    }
}
