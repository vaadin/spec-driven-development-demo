package com.example.specdriven.ticket;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import java.util.List;

@BrowserCallable
@AnonymousAllowed
public class TicketEndpoint {

    private final TicketService ticketService;

    public TicketEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public record SeatStatus(int row, int number, boolean sold) {}

    public record PurchaseRequest(Long showId, List<int[]> seats, String customerName, String customerEmail) {}

    public record PurchaseResult(boolean success, String message, List<TicketInfo> tickets) {}

    public record TicketInfo(int seatRow, int seatNumber, String customerName) {}

    public List<SeatStatus> getSeatStatuses(Long showId) {
        List<Ticket> tickets = ticketService.findByShowId(showId);
        return tickets.stream()
                .map(t -> new SeatStatus(t.getSeatRow(), t.getSeatNumber(), true))
                .toList();
    }

    public PurchaseResult purchaseTickets(Long showId, List<int[]> seats,
            String customerName, String customerEmail) {
        try {
            List<Ticket> purchased = ticketService.purchaseTickets(showId, seats, customerName, customerEmail);
            List<TicketInfo> infos = purchased.stream()
                    .map(t -> new TicketInfo(t.getSeatRow(), t.getSeatNumber(), t.getCustomerName()))
                    .toList();
            return new PurchaseResult(true, "Tickets purchased successfully!", infos);
        } catch (Exception e) {
            return new PurchaseResult(false, e.getMessage(), List.of());
        }
    }
}
