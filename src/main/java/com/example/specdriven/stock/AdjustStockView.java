package com.example.specdriven.stock;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("adjust")
@PageTitle("Adjust Stock — Stash.log")
@RolesAllowed("ADMIN")
public class AdjustStockView extends VerticalLayout {

    private final ProductService productService;
    private final StockService stockService;

    final ComboBox<Product> productSelect = new ComboBox<>("Product");
    final Span currentStockDisplay = new Span();
    final IntegerField adjustmentField = new IntegerField("Adjustment Quantity");
    final TextArea reasonField = new TextArea("Reason");
    final Button applyButton = new Button("Apply Adjustment");

    public AdjustStockView(ProductService productService, StockService stockService) {
        this.productService = productService;
        this.stockService = stockService;
        setPadding(true);

        configureForm();

        FormLayout form = new FormLayout();
        form.add(productSelect, currentStockDisplay, adjustmentField, reasonField, applyButton);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.setMaxWidth("500px");

        add(form);
    }

    private void configureForm() {
        productSelect.setItems(productService.findAll());
        productSelect.setItemLabelGenerator(p -> p.getName() + " (" + p.getSku() + ")");
        productSelect.setRequiredIndicatorVisible(true);
        productSelect.setWidthFull();
        productSelect.addValueChangeListener(e -> updateCurrentStockDisplay());

        currentStockDisplay.setText("Current Stock: —");
        currentStockDisplay.getStyle()
                .set("font-size", "var(--aura-font-size-l)")
                .set("font-weight", "var(--aura-font-weight-semibold)")
                .set("padding", "var(--vaadin-padding-s) 0");

        adjustmentField.setStepButtonsVisible(true);
        adjustmentField.setRequiredIndicatorVisible(true);
        adjustmentField.setHelperText("Positive to add, negative to subtract");

        reasonField.setRequiredIndicatorVisible(true);
        reasonField.setPlaceholder("e.g., Damaged goods, Cycle count correction");
        reasonField.setMinLength(3);

        applyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        applyButton.addClickListener(e -> applyAdjustment());
    }

    private void updateCurrentStockDisplay() {
        Product product = productSelect.getValue();
        if (product != null) {
            currentStockDisplay.setText("Current Stock: " + product.getCurrentStock());
        } else {
            currentStockDisplay.setText("Current Stock: —");
        }
    }

    private void applyAdjustment() {
        boolean valid = true;

        if (productSelect.getValue() == null) {
            productSelect.setInvalid(true);
            productSelect.setErrorMessage("Please select a product");
            valid = false;
        } else {
            productSelect.setInvalid(false);
        }

        if (adjustmentField.getValue() == null || adjustmentField.getValue() == 0) {
            adjustmentField.setInvalid(true);
            adjustmentField.setErrorMessage("Adjustment quantity must be non-zero");
            valid = false;
        } else {
            adjustmentField.setInvalid(false);
        }

        String reason = reasonField.getValue();
        if (reason == null || reason.trim().length() < 3) {
            reasonField.setInvalid(true);
            reasonField.setErrorMessage("Reason must be at least 3 characters");
            valid = false;
        } else {
            reasonField.setInvalid(false);
        }

        if (!valid) return;

        Product product = productSelect.getValue();
        int oldStock = product.getCurrentStock();
        int adjustment = adjustmentField.getValue();

        try {
            stockService.adjustStock(product, adjustment, reason.trim());
            int newStock = product.getCurrentStock();

            Notification notification = Notification.show(
                    "Stock adjusted for " + product.getName()
                    + ": " + oldStock + " → " + newStock, 3000,
                    Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Reset form
            productSelect.clear();
            adjustmentField.clear();
            reasonField.clear();
            updateCurrentStockDisplay();
            productSelect.setItems(productService.findAll());
        } catch (IllegalArgumentException ex) {
            Notification notification = Notification.show(
                    ex.getMessage(), 0,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
