package com.example.specdriven.orders.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class FlowerCatalogTest {

    private final FlowerCatalog catalog = new FlowerCatalog();

    @Test
    void hardCodedCatalogHasFourFlowersEachWithThreeColors() {
        List<Flower> flowers = catalog.getFlowers();
        assertEquals(4, flowers.size(), "BR-01: catalog has four flowers");
        for (Flower flower : flowers) {
            assertEquals(3, flower.colors().size(),
                    "BR-01: each flower has exactly three colors (" + flower.name() + ")");
        }
    }

    @Test
    void catalogMatchesSpecifiedFlowersAndColors() {
        Map<String, List<String>> byName = catalog.getFlowers().stream()
                .collect(Collectors.toMap(Flower::name, Flower::colors));
        assertEquals(List.of("Red", "Pink", "White"), byName.get("Rose"));
        assertEquals(List.of("Red", "Yellow", "Purple"), byName.get("Tulip"));
        assertEquals(List.of("White", "Pink", "Orange"), byName.get("Lily"));
        assertEquals(List.of("Yellow", "Orange", "Red"), byName.get("Sunflower"));
    }

    @Test
    void catalogIsImmutable() {
        List<Flower> flowers = catalog.getFlowers();
        assertTrue(flowers == catalog.getFlowers(), "same instance is returned each call");
    }
}
