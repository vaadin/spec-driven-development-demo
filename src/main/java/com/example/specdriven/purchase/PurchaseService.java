package com.example.specdriven.purchase;

import com.example.specdriven.show.Show;
import com.example.specdriven.show.ShowRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ShowRepository showRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, ShowRepository showRepository) {
        this.purchaseRepository = purchaseRepository;
        this.showRepository = showRepository;
    }

    @Transactional
    public Purchase createPurchase(Show show, int quantity, String cardLastFour) {
        show.setAvailableSeats(show.getAvailableSeats() - quantity);
        showRepository.save(show);

        Purchase purchase = new Purchase();
        purchase.setConfirmationCode(UUID.randomUUID().toString());
        purchase.setShow(show);
        purchase.setQuantity(quantity);
        purchase.setTotalPrice(show.getPrice().multiply(BigDecimal.valueOf(quantity)));
        purchase.setCardLastFour(cardLastFour);
        purchase.setPurchasedAt(LocalDateTime.now());
        return purchaseRepository.save(purchase);
    }

    public Optional<Purchase> findByConfirmationCode(String confirmationCode) {
        return purchaseRepository.findByConfirmationCode(confirmationCode);
    }
}
