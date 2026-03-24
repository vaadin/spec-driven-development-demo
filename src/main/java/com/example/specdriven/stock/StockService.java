package com.example.specdriven.stock;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockEventRepository stockEventRepository;
    private final ProductRepository productRepository;

    public StockService(StockEventRepository stockEventRepository,
                        ProductRepository productRepository) {
        this.stockEventRepository = stockEventRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public StockEvent receiveStock(Product product, int quantity, String reference) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive integer");
        }

        product.setCurrentStock(product.getCurrentStock() + quantity);
        productRepository.save(product);

        StockEvent event = new StockEvent();
        event.setProduct(product);
        event.setType(StockEventType.RECEIVED);
        event.setQuantity(quantity);
        event.setReason(reference);
        return stockEventRepository.save(event);
    }

    @Transactional
    public StockEvent adjustStock(Product product, int quantity, String reason) {
        if (quantity == 0) {
            throw new IllegalArgumentException("Adjustment quantity must be non-zero");
        }
        if (reason == null || reason.trim().length() < 3) {
            throw new IllegalArgumentException("Reason must be at least 3 characters");
        }

        int newStock = product.getCurrentStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Adjustment would bring stock below zero (current: "
                    + product.getCurrentStock() + ", adjustment: " + quantity + ")");
        }

        int oldStock = product.getCurrentStock();
        product.setCurrentStock(newStock);
        productRepository.save(product);

        StockEvent event = new StockEvent();
        event.setProduct(product);
        event.setType(StockEventType.ADJUSTMENT);
        event.setQuantity(quantity);
        event.setReason(reason);
        return stockEventRepository.save(event);
    }

    public List<StockEvent> findRecentEvents() {
        return stockEventRepository.findTop10ByOrderByTimestampDesc();
    }
}
