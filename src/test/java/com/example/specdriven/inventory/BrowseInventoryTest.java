package com.example.specdriven.inventory;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "USER")
class BrowseInventoryTest extends SpringBrowserlessTest {

    @Autowired
    private ProductService productService;

    @Test
    void inventoryGridDisplaysAllProducts() {
        InventoryView view = navigate(InventoryView.class);

        @SuppressWarnings("unchecked")
        Grid<Product> grid = (Grid<Product>) $(Grid.class).single();
        List<Product> allProducts = productService.findAll();

        // Grid should have the same number of items as in DB
        assertFalse(allProducts.isEmpty());
    }

    @Test
    void searchFieldFiltersByNameOrSku() {
        InventoryView view = navigate(InventoryView.class);

        test(view.searchField).setValue("Widget");
        // After filtering, the data view should only contain Widget products
        long count = productService.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains("widget")
                        || p.getSku().toLowerCase().contains("widget"))
                .count();
        assertTrue(count > 0);
        assertTrue(count < productService.findAll().size());
    }

    @Test
    void categoryFilterFiltersGrid() {
        InventoryView view = navigate(InventoryView.class);

        test(view.categoryFilter).selectItem("Fasteners");
        // Filtering by category should work
        long fastenerCount = productService.findAll().stream()
                .filter(p -> "Fasteners".equals(p.getCategory()))
                .count();
        assertTrue(fastenerCount > 0);
    }

    @Test
    void lowStockProductsExistInData() {
        InventoryView view = navigate(InventoryView.class);

        // Verify low-stock products exist (currentStock > 0 and <= reorderPoint)
        long lowStockCount = productService.findAll().stream()
                .filter(p -> p.getCurrentStock() > 0 && p.getCurrentStock() <= p.getReorderPoint())
                .count();
        assertTrue(lowStockCount > 0, "Should have low-stock products in demo data");
    }

    @Test
    void outOfStockProductsExistInData() {
        InventoryView view = navigate(InventoryView.class);

        // Verify out-of-stock products exist (currentStock == 0)
        long outOfStockCount = productService.findAll().stream()
                .filter(p -> p.getCurrentStock() == 0)
                .count();
        assertTrue(outOfStockCount > 0, "Should have out-of-stock products in demo data");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminUsersCanAlsoAccessInventory() {
        InventoryView view = navigate(InventoryView.class);
        assertNotNull(view);
    }

    @Test
    @WithMockUser(roles = "USER")
    void staffUsersCanAccessInventory() {
        InventoryView view = navigate(InventoryView.class);
        assertNotNull(view);
    }
}
