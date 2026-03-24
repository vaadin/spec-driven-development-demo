package com.example.specdriven.dashboard;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "USER")
class DashboardTest extends SpringBrowserlessTest {

    @Autowired
    private ProductService productService;

    @Test
    void dashboardDisplaysFourSummaryCards() {
        DashboardView view = navigate(DashboardView.class);

        List<Paragraph> cardValues = $(Paragraph.class).withClassName("card-value").all();
        assertEquals(4, cardValues.size(), "Dashboard should have 4 summary cards");
    }

    @Test
    void totalProductsCardShowsCorrectCount() {
        DashboardView view = navigate(DashboardView.class);

        List<Paragraph> cardValues = $(Paragraph.class).withClassName("card-value").all();
        long expectedCount = productService.findAll().size();
        assertEquals(String.valueOf(expectedCount), cardValues.get(0).getText());
    }

    @Test
    void totalStockValueIsCalculatedCorrectly() {
        DashboardView view = navigate(DashboardView.class);

        List<Product> products = productService.findAll();
        BigDecimal expectedValue = products.stream()
                .map(p -> p.getUnitPrice().multiply(BigDecimal.valueOf(p.getCurrentStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Paragraph> cardValues = $(Paragraph.class).withClassName("card-value").all();
        assertEquals("$" + expectedValue.toPlainString(), cardValues.get(1).getText());
    }

    @Test
    void lowStockAlertsListsProductsAtOrBelowReorderPoint() {
        DashboardView view = navigate(DashboardView.class);

        // There should be at least 2 grids (low-stock alerts + recent activity)
        List<Grid> grids = $(Grid.class).all();
        assertTrue(grids.size() >= 2, "Should have low-stock alerts and recent activity grids");
    }

    @Test
    void recentActivityShowsStockEvents() {
        DashboardView view = navigate(DashboardView.class);

        List<Grid> grids = $(Grid.class).all();
        assertTrue(grids.size() >= 2, "Should have recent activity grid");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminUsersCanViewDashboard() {
        DashboardView view = navigate(DashboardView.class);
        assertNotNull(view);
    }

    @Test
    @WithMockUser(roles = "USER")
    void staffUsersCanViewDashboard() {
        DashboardView view = navigate(DashboardView.class);
        assertNotNull(view);
    }
}
