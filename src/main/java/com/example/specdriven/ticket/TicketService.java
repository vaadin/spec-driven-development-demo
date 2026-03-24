package com.example.specdriven.ticket;

import com.example.specdriven.show.Show;
import com.example.specdriven.show.ShowRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ShowRepository showRepository;

    public TicketService(TicketRepository ticketRepository, ShowRepository showRepository) {
        this.ticketRepository = ticketRepository;
        this.showRepository = showRepository;
    }

    public List<Ticket> findByShowId(Long showId) {
        return ticketRepository.findByShowId(showId);
    }

    @Transactional
    public List<Ticket> purchaseTickets(Long showId, List<int[]> seats,
            String customerName, String customerEmail) {
        if (seats.size() > 6) {
            throw new IllegalArgumentException("Maximum 6 tickets per transaction");
        }
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("No seats selected");
        }

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found"));

        List<Ticket> tickets = new ArrayList<>();
        for (int[] seat : seats) {
            int row = seat[0];
            int number = seat[1];

            if (ticketRepository.existsByShowIdAndSeatRowAndSeatNumber(showId, row, number)) {
                throw new IllegalStateException(
                        "Seat " + (char) ('A' + row - 1) + number + " is already sold. Please refresh and try again.");
            }

            Ticket ticket = new Ticket();
            ticket.setShow(show);
            ticket.setSeatRow(row);
            ticket.setSeatNumber(number);
            ticket.setCustomerName(customerName);
            ticket.setCustomerEmail(customerEmail);
            ticket.setPurchasedAt(LocalDateTime.now());
            tickets.add(ticketRepository.save(ticket));
        }

        return tickets;
    }
}
