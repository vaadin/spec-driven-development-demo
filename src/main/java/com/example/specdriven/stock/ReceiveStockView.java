package com.example.specdriven.stock;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("receive")
@PageTitle("Receive Stock — Stash.log")
@PermitAll
public class ReceiveStockView extends VerticalLayout {

    private final ProductService productService;
    private final StockService stockService;

    final ComboBox<Product> productSelect = new ComboBox<>("Product");
    final IntegerField quantityField = new IntegerField("Quantity");
    final TextField referenceField = new TextField("Reference (optional)");
    final Button receiveButton = new Button("Receive");

    public ReceiveStockView(ProductService productService, StockService stockService) {
        this.productService = productService;
        this.stockService = stockService;
        setPadding(true);

        configureForm();

        FormLayout form = new FormLayout();
        form.add(productSelect, quantityField, referenceField, receiveButton);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        form.setMaxWidth("500px");

        add(form);
    }

    private void configureForm() {
        productSelect.setItems(productService.findAll());
        productSelect.setItemLabelGenerator(p -> p.getName() + " (" + p.getSku() + ")");
        productSelect.setRequiredIndicatorVisible(true);
        productSelect.setWidthFull();

        quantityField.setMin(1);
        quantityField.setStepButtonsVisible(true);
        quantityField.setRequiredIndicatorVisible(true);

        referenceField.setPlaceholder("e.g., PO number or supplier name");

        receiveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        receiveButton.addClickListener(e -> receiveStock());
    }

    private void receiveStock() {
        boolean valid = true;

        if (productSelect.getValue() == null) {
            productSelect.setInvalid(true);
            productSelect.setErrorMessage("Please select a product");
            valid = false;
        } else {
            productSelect.setInvalid(false);
        }

        if (quantityField.getValue() == null || quantityField.getValue() < 1) {
            quantityField.setInvalid(true);
            quantityField.setErrorMessage("Quantity must be at least 1");
            valid = false;
        } else {
            quantityField.setInvalid(false);
        }

        if (!valid) return;

        Product product = productSelect.getValue();
        int quantity = quantityField.getValue();
        String reference = referenceField.getValue();

        try {
            stockService.receiveStock(product, quantity, reference);
            int newStock = product.getCurrentStock();

            Notification notification = Notification.show(
                    "Received " + quantity + " × " + product.getName()
                    + ". New stock level: " + newStock, 3000,
                    Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Reset form
            productSelect.clear();
            quantityField.clear();
            referenceField.clear();
            // Refresh product list to show updated stock
            productSelect.setItems(productService.findAll());
        } catch (Exception ex) {
            Notification notification = Notification.show(
                    "Error: " + ex.getMessage(), 0,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
