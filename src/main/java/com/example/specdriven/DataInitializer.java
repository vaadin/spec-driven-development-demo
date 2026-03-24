package com.example.specdriven;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductRepository;
import com.example.specdriven.stock.StockEvent;
import com.example.specdriven.stock.StockEventRepository;
import com.example.specdriven.stock.StockEventType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final StockEventRepository stockEventRepository;

    public DataInitializer(ProductRepository productRepository,
                           StockEventRepository stockEventRepository) {
        this.productRepository = productRepository;
        this.stockEventRepository = stockEventRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        Product p1 = createProduct("Widget A", "WGT-001", "Widgets",
                new BigDecimal("12.99"), 10, 50);
        Product p2 = createProduct("Widget B", "WGT-002", "Widgets",
                new BigDecimal("24.50"), 5, 3);
        Product p3 = createProduct("Bolt M8x20", "BLT-008", "Fasteners",
                new BigDecimal("0.35"), 100, 250);
        Product p4 = createProduct("Nut M8", "NUT-008", "Fasteners",
                new BigDecimal("0.15"), 100, 80);
        Product p5 = createProduct("Packing Tape", "PKG-001", "Packaging",
                new BigDecimal("4.75"), 20, 0);
        Product p6 = createProduct("Bubble Wrap Roll", "PKG-002", "Packaging",
                new BigDecimal("18.00"), 5, 12);
        Product p7 = createProduct("Safety Goggles", "SAF-001", "Safety",
                new BigDecimal("8.50"), 15, 15);
        Product p8 = createProduct("Work Gloves", "SAF-002", "Safety",
                new BigDecimal("6.25"), 20, 45);

        createStockEvent(p1, StockEventType.RECEIVED, 50, "Initial stock", 1);
        createStockEvent(p2, StockEventType.RECEIVED, 10, "PO-2024-001", 3);
        createStockEvent(p2, StockEventType.ADJUSTMENT, -7, "Damaged goods", 2);
        createStockEvent(p3, StockEventType.RECEIVED, 250, "Bulk order", 4);
        createStockEvent(p5, StockEventType.ADJUSTMENT, -5, "Cycle count correction", 1);
        createStockEvent(p7, StockEventType.RECEIVED, 15, "Initial stock", 5);
    }

    private Product createProduct(String name, String sku, String category,
                                  BigDecimal unitPrice, int reorderPoint, int currentStock) {
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setCategory(category);
        product.setUnitPrice(unitPrice);
        product.setReorderPoint(reorderPoint);
        product.setCurrentStock(currentStock);
        return productRepository.save(product);
    }

    private void createStockEvent(Product product, StockEventType type,
                                  int quantity, String reason, int daysAgo) {
        StockEvent event = new StockEvent();
        event.setProduct(product);
        event.setType(type);
        event.setQuantity(quantity);
        event.setReason(reason);
        event.setTimestamp(LocalDateTime.now().minusDays(daysAgo));
        stockEventRepository.save(event);
    }
}
