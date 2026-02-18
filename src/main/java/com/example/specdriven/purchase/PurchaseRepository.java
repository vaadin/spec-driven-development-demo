package com.example.specdriven.purchase;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByConfirmationCode(String confirmationCode);
}
