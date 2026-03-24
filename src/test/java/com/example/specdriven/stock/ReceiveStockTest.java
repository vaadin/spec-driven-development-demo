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
@WithMockUser(roles = "USER")
class ReceiveStockTest extends SpringBrowserlessTest {

    @Autowired
    private ProductService productService;

    @Test
    void successfulReceiptIncreasesStockLevel() {
        ReceiveStockView view = navigate(ReceiveStockView.class);
        Product product = productService.findAll().get(0);
        int originalStock = product.getCurrentStock();

        test(view.productSelect).selectItem(product.getName() + " (" + product.getSku() + ")");
        test(view.quantityField).setValue(5);
        test(view.referenceField).setValue("PO-TEST-001");
        test(view.receiveButton).click();

        Notification notification = $(Notification.class).single();
        String text = test(notification).getText();
        assertTrue(text.contains(product.getName()));
        assertTrue(text.contains(String.valueOf(originalStock + 5)));

        Product updated = productService.findById(product.getId()).orElseThrow();
        assertEquals(originalStock + 5, updated.getCurrentStock());
    }

    @Test
    void formResetsAfterSuccessfulSubmission() {
        ReceiveStockView view = navigate(ReceiveStockView.class);
        Product product = productService.findAll().get(0);

        test(view.productSelect).selectItem(product.getName() + " (" + product.getSku() + ")");
        test(view.quantityField).setValue(1);
        test(view.receiveButton).click();

        assertNull(view.productSelect.getValue());
        assertNull(view.quantityField.getValue());
    }

    @Test
    void submittingWithoutProductShowsValidationError() {
        ReceiveStockView view = navigate(ReceiveStockView.class);

        test(view.quantityField).setValue(5);
        test(view.receiveButton).click();

        assertTrue(view.productSelect.isInvalid());
    }

    @Test
    void submittingWithoutQuantityShowsValidationError() {
        ReceiveStockView view = navigate(ReceiveStockView.class);
        Product product = productService.findAll().get(0);

        test(view.productSelect).selectItem(product.getName() + " (" + product.getSku() + ")");
        // Leave quantity empty (null)
        test(view.receiveButton).click();

        assertTrue(view.quantityField.isInvalid());
    }
}
