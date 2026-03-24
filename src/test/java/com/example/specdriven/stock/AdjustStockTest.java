package com.example.specdriven.stock;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class AdjustStockTest extends SpringBrowserlessTest {

    @Autowired
    private ProductService productService;

    @Test
    void currentStockIsDisplayedWhenProductSelected() {
        AdjustStockView view = navigate(AdjustStockView.class);
        Product product = productService.findAll().get(0);

        test(view.productSelect).selectItem(product.getName() + " (" + product.getSku() + ")");

        assertTrue(view.currentStockDisplay.getText().contains(
                String.valueOf(product.getCurrentStock())));
    }

    @Test
    void positiveAdjustmentIncreasesStock() {
        AdjustStockView view = navigate(AdjustStockView.class);
        Product product = productService.findAll().stream()
                .filter(p -> p.getCurrentStock() > 0)
                .findFirst().orElseThrow();
        int originalStock = product.getCurrentStock();

        test(view.productSelect).selectItem(product.getName() + " (" + product.getSku() + ")");
        test(view.adjustmentField).setValue(5);
        test(view.reasonField).setValue("Test positive adjustment");
        test(view.applyButton).click();

        Notification notification = $(Notification.class).single();
        String text = test(notification).getText();
        assertTrue(text.contains(String.valueOf(originalStock + 5)));

        Product updated = productService.findById(product.getId()).orElseThrow();
        assertEquals(originalStock + 5, updated.getCurrentStock());
    }

    @Test
    void negativeAdjustmentDecreasesStock() {
        AdjustStockView view = navigate(AdjustStockView.class);
        Product product = productService.findAll().stream()
                .filter(p -> p.getCurrentStock() >= 5)
                .findFirst().orElseThrow();
        int originalStock = product.getCurrentStock();

        test(view.productSelect).selectItem(product.getName() + " (" + product.getSku() + ")");
        test(view.adjustmentField).setValue(-2);
        test(view.reasonField).setValue("Damaged goods found");
        test(view.applyButton).click();

        Product updated = productService.findById(product.getId()).orElseThrow();
        assertEquals(originalStock - 2, updated.getCurrentStock());
    }

    @Test
    void missingReasonShowsValidationError() {
        AdjustStockView view = navigate(AdjustStockView.class);
        Product product = productService.findAll().get(0);

        test(view.productSelect).selectItem(product.getName() + " (" + product.getSku() + ")");
        test(view.adjustmentField).setValue(1);
        // Leave reason empty
        test(view.applyButton).click();

        assertTrue(view.reasonField.isInvalid());
    }

    @Test
    void successNotificationShowsOldAndNewStockLevels() {
        AdjustStockView view = navigate(AdjustStockView.class);
        Product product = productService.findAll().stream()
                .filter(p -> p.getCurrentStock() > 0)
                .findFirst().orElseThrow();
        int oldStock = product.getCurrentStock();

        test(view.productSelect).selectItem(product.getName() + " (" + product.getSku() + ")");
        test(view.adjustmentField).setValue(3);
        test(view.reasonField).setValue("Cycle count");
        test(view.applyButton).click();

        Notification notification = $(Notification.class).single();
        String text = test(notification).getText();
        assertTrue(text.contains(String.valueOf(oldStock)));
        assertTrue(text.contains(String.valueOf(oldStock + 3)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminUsersCannotAccessAdjustStockView() {
        assertThrows(Exception.class, () -> navigate(AdjustStockView.class));
    }
}
