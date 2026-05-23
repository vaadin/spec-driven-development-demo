package com.example.specdriven.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.specdriven.orders.domain.Order;
import com.example.specdriven.orders.domain.OrderRepository;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void clearOrders() {
        orderRepository.deleteAll();
    }

    @Test
    void placesOrderAndPersistsLines() {
        Order saved = orderService.placeOrder("Alice", "555-1234", List.of(
                new OrderLineRequest("Rose", "Red", 3),
                new OrderLineRequest("Tulip", "Yellow", 2)));

        assertNotNull(saved.getId(), "Order is persisted with an id");
        assertEquals("Alice", saved.getCustomerName());
        assertEquals("555-1234", saved.getPhoneNumber());
        assertNotNull(saved.getCreatedAt());
        assertEquals(2, saved.getLines().size());

        Order reloaded = orderRepository.findById(saved.getId()).orElseThrow();
        assertEquals(2, reloaded.getLines().size());
        assertEquals(3, reloaded.getLines().get(0).getQuantity());
        assertEquals("Rose", reloaded.getLines().get(0).getFlower());
        assertEquals("Red", reloaded.getLines().get(0).getColor());
    }

    @Test
    void trimsNameAndPhone() {
        Order saved = orderService.placeOrder("  Alice  ", "  555  ",
                List.of(new OrderLineRequest("Rose", "Red", 1)));
        assertEquals("Alice", saved.getCustomerName());
        assertEquals("555", saved.getPhoneNumber());
    }

    @Test
    void rejectsBlankName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder(" ", "555",
                        List.of(new OrderLineRequest("Rose", "Red", 1))));
        assertEquals("Name is required", ex.getMessage());
        assertEquals(0, orderRepository.count(), "Nothing persisted on failure");
    }

    @Test
    void rejectsBlankPhone() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("Alice", "",
                        List.of(new OrderLineRequest("Rose", "Red", 1))));
        assertEquals("Phone number is required", ex.getMessage());
        assertEquals(0, orderRepository.count());
    }

    @Test
    void rejectsOrderWithNoPositiveQuantity() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("Alice", "555", List.of(
                        new OrderLineRequest("Rose", "Red", 0),
                        new OrderLineRequest("Tulip", "Yellow", 0))));
        assertEquals("At least one flower must be selected", ex.getMessage());
        assertEquals(0, orderRepository.count());
    }

    @Test
    void filtersOutZeroQuantityLines() {
        Order saved = orderService.placeOrder("Alice", "555", List.of(
                new OrderLineRequest("Rose", "Red", 2),
                new OrderLineRequest("Rose", "Pink", 0),
                new OrderLineRequest("Tulip", "Yellow", 5)));
        assertEquals(2, saved.getLines().size(), "Zero-quantity cells are excluded");
    }

    @Test
    void supportsLargeQuantities() {
        Order saved = orderService.placeOrder("Alice", "555",
                List.of(new OrderLineRequest("Sunflower", "Yellow", 9999)));
        assertEquals(9999, saved.getLines().get(0).getQuantity(),
                "BR-05: no upper bound on quantity");
    }
}
