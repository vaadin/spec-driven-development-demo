package com.example.specdriven.orders.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.specdriven.orders.domain.Order;
import com.example.specdriven.orders.domain.OrderLine;
import com.example.specdriven.orders.domain.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Order placeOrder(String customerName, String phoneNumber, List<OrderLineRequest> lines) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        List<OrderLineRequest> nonEmpty = lines.stream().filter(l -> l.quantity() > 0).toList();
        if (nonEmpty.isEmpty()) {
            throw new IllegalArgumentException("At least one flower must be selected");
        }

        Order order = new Order();
        order.setCustomerName(customerName.trim());
        order.setPhoneNumber(phoneNumber.trim());
        order.setCreatedAt(Instant.now());
        for (OrderLineRequest request : nonEmpty) {
            OrderLine line = new OrderLine();
            line.setFlower(request.flower());
            line.setColor(request.color());
            line.setQuantity(request.quantity());
            order.addLine(line);
        }
        return repository.save(order);
    }
}
