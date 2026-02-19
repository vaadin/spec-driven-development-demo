package com.example.specdriven.tickets;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final TicketRepository ticketRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, TicketRepository ticketRepository) {
        this.purchaseRepository = purchaseRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public PurchaseOrder createPurchase(Long ticketId, int quantity, String cardLastFour) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        PurchaseOrder order = new PurchaseOrder();
        order.setConfirmationCode(UUID.randomUUID());
        order.setTicket(ticket);
        order.setQuantity(quantity);
        order.setTotalPrice(ticket.getPrice().multiply(BigDecimal.valueOf(quantity)));
        order.setCardLastFour(cardLastFour);
        order.setPurchasedAt(LocalDateTime.now());

        return purchaseRepository.save(order);
    }

    public Optional<PurchaseOrder> findByConfirmationCode(UUID confirmationCode) {
        return purchaseRepository.findByConfirmationCode(confirmationCode);
    }
}
