package com.example.specdriven.product;

import com.example.specdriven.stock.StockEvent;
import com.example.specdriven.stock.StockEventRepository;
import com.example.specdriven.stock.StockEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockEventRepository stockEventRepository;

    @Test
    void canSaveAndFindProduct() {
        Product p = new Product();
        p.setName("Service Test");
        p.setSku("SVC-001");
        p.setUnitPrice(new BigDecimal("5.00"));
        p.setReorderPoint(0);
        p.setCurrentStock(0);
        Product saved = productService.save(p);

        assertNotNull(saved.getId());
        assertTrue(productService.findById(saved.getId()).isPresent());
        assertTrue(productService.findBySku("SVC-001").isPresent());
    }

    @Test
    void isSkuTakenReturnsTrueForExistingSku() {
        Product p = new Product();
        p.setName("SKU Test");
        p.setSku("SKU-UNIQUE");
        p.setUnitPrice(new BigDecimal("1.00"));
        p.setReorderPoint(0);
        p.setCurrentStock(0);
        productService.save(p);

        assertTrue(productService.isSkuTaken("SKU-UNIQUE", null));
        assertFalse(productService.isSkuTaken("SKU-UNIQUE", p.getId()));
        assertFalse(productService.isSkuTaken("NONEXISTENT", null));
    }

    @Test
    void hasStockEventsReturnsTrueWhenEventsExist() {
        Product p = new Product();
        p.setName("Event Test");
        p.setSku("EVT-001");
        p.setUnitPrice(new BigDecimal("1.00"));
        p.setReorderPoint(0);
        p.setCurrentStock(10);
        productService.save(p);

        assertFalse(productService.hasStockEvents(p));

        StockEvent event = new StockEvent();
        event.setProduct(p);
        event.setType(StockEventType.RECEIVED);
        event.setQuantity(10);
        stockEventRepository.save(event);

        assertTrue(productService.hasStockEvents(p));
    }

    @Test
    void findAllCategoriesReturnsDistinctSortedCategories() {
        List<String> categories = productService.findAllCategories();
        assertFalse(categories.isEmpty());
        // Check sorted
        for (int i = 1; i < categories.size(); i++) {
            assertTrue(categories.get(i).compareTo(categories.get(i - 1)) >= 0);
        }
        // Check distinct
        assertEquals(categories.stream().distinct().count(), categories.size());
    }
}
