package com.example.specdriven.stock;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    @Test
    void receiveStockIncreasesProductStock() {
        Product product = productService.findAll().get(0);
        int originalStock = product.getCurrentStock();

        StockEvent event = stockService.receiveStock(product, 10, "Test receipt");

        assertEquals(StockEventType.RECEIVED, event.getType());
        assertEquals(10, event.getQuantity());
        assertEquals(originalStock + 10, product.getCurrentStock());
    }

    @Test
    void receiveStockRejectsZeroQuantity() {
        Product product = productService.findAll().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> stockService.receiveStock(product, 0, "Test"));
    }

    @Test
    void receiveStockRejectsNegativeQuantity() {
        Product product = productService.findAll().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> stockService.receiveStock(product, -1, "Test"));
    }

    @Test
    void adjustStockPositiveIncreasesStock() {
        Product product = productService.findAll().get(0);
        int originalStock = product.getCurrentStock();

        StockEvent event = stockService.adjustStock(product, 5, "Test adjustment");

        assertEquals(StockEventType.ADJUSTMENT, event.getType());
        assertEquals(originalStock + 5, product.getCurrentStock());
    }

    @Test
    void adjustStockNegativeDecreasesStock() {
        Product product = productService.findAll().stream()
                .filter(p -> p.getCurrentStock() >= 5)
                .findFirst().orElseThrow();
        int originalStock = product.getCurrentStock();

        StockEvent event = stockService.adjustStock(product, -3, "Damaged goods");

        assertEquals(originalStock - 3, product.getCurrentStock());
    }

    @Test
    void adjustStockRejectsZeroQuantity() {
        Product product = productService.findAll().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> stockService.adjustStock(product, 0, "Test"));
    }

    @Test
    void adjustStockRejectsBelowZeroResult() {
        Product product = productService.findAll().stream()
                .filter(p -> p.getCurrentStock() < 1000)
                .findFirst().orElseThrow();
        int stock = product.getCurrentStock();

        assertThrows(IllegalArgumentException.class,
                () -> stockService.adjustStock(product, -(stock + 1), "Too much"));
    }

    @Test
    void adjustStockRejectsShortReason() {
        Product product = productService.findAll().get(0);
        assertThrows(IllegalArgumentException.class,
                () -> stockService.adjustStock(product, 1, "ab"));
    }

    @Test
    void findRecentEventsReturnsResults() {
        assertFalse(stockService.findRecentEvents().isEmpty());
    }
}
