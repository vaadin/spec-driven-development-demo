package com.example.specdriven.orders.domain;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FlowerCatalog {

    private static final List<Flower> FLOWERS = List.of(
            new Flower("Rose", List.of("Red", "Pink", "White")),
            new Flower("Tulip", List.of("Red", "Yellow", "Purple")),
            new Flower("Lily", List.of("White", "Pink", "Orange")),
            new Flower("Sunflower", List.of("Yellow", "Orange", "Red")));

    public List<Flower> getFlowers() {
        return FLOWERS;
    }
}
