package com.example.specdriven.product;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class ManageProductsTest extends SpringBrowserlessTest {

    @Autowired
    private ProductService productService;

    @Test
    void newProductCanBeCreatedAndAppearsInGrid() {
        ProductsView view = navigate(ProductsView.class);

        test(view.addButton).click();
        test(view.nameField).setValue("New Widget");
        test(view.skuField).setValue("NEW-001");
        test(view.categoryField).setValue("Widgets");
        test(view.unitPriceField).setValue(new BigDecimal("15.99"));

        test(view.saveButton).click();

        Notification notification = $(Notification.class).single();
        assertTrue(test(notification).getText().contains("Product saved"));
        assertTrue(productService.findBySku("NEW-001").isPresent());
    }

    @Test
    void duplicateSkuShowsValidationError() {
        ProductsView view = navigate(ProductsView.class);
        String existingSku = productService.findAll().get(0).getSku();

        test(view.addButton).click();
        test(view.nameField).setValue("Duplicate");
        test(view.skuField).setValue(existingSku);
        test(view.unitPriceField).setValue(new BigDecimal("10.00"));

        test(view.saveButton).click();

        assertTrue(view.skuField.isInvalid());
        assertEquals("SKU already exists", view.skuField.getErrorMessage());
    }

    @Test
    void missingMandatoryFieldsShowValidationErrors() {
        ProductsView view = navigate(ProductsView.class);

        test(view.addButton).click();
        test(view.saveButton).click();

        assertTrue(view.nameField.isInvalid());
        assertTrue(view.skuField.isInvalid());
        assertTrue(view.unitPriceField.isInvalid());
    }

    @Test
    void unitPriceRejectsZeroAndNegativeValues() {
        ProductsView view = navigate(ProductsView.class);

        test(view.addButton).click();
        test(view.nameField).setValue("Test");
        test(view.skuField).setValue("ZERO-001");
        test(view.unitPriceField).setValue(BigDecimal.ZERO);

        test(view.saveButton).click();

        assertTrue(view.unitPriceField.isInvalid());
        assertEquals("Unit price must be a positive number", view.unitPriceField.getErrorMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    void existingProductCanBeEditedAndSaved() {
        ProductsView view = navigate(ProductsView.class);
        Product first = productService.findAll().get(0);

        Grid<Product> grid = (Grid<Product>) $(Grid.class).single();
        test(grid).clickRow(0);

        test(view.nameField).setValue("Updated Name");
        test(view.saveButton).click();

        Notification notification = $(Notification.class).single();
        assertTrue(test(notification).getText().contains("Product saved"));

        Product updated = productService.findById(first.getId()).orElseThrow();
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void productWithStockEventsCannotBeDeleted() {
        ProductsView view = navigate(ProductsView.class);

        // First product from demo data has stock events
        @SuppressWarnings("unchecked")
        Grid<Product> grid = (Grid<Product>) $(Grid.class).single();
        test(grid).clickRow(0);

        test(view.deleteButton).click();

        // Should show error notification, not a confirm dialog
        Notification notification = $(Notification.class).single();
        assertTrue(test(notification).getText().contains("Cannot delete"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminUsersCannotAccessManageProductsView() {
        // Staff users (USER role only) should not be able to navigate to the products view
        assertThrows(Exception.class, () -> navigate(ProductsView.class));
    }
}
