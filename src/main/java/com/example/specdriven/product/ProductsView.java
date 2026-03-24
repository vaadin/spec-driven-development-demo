package com.example.specdriven.product;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;

@Route("products")
@PageTitle("Products — Stash.log")
@RolesAllowed("ADMIN")
public class ProductsView extends VerticalLayout {

    private final ProductService productService;

    final Grid<Product> grid = new Grid<>(Product.class, false);
    final TextField nameField = new TextField("Name");
    final TextField skuField = new TextField("SKU");
    final TextField categoryField = new TextField("Category");
    final BigDecimalField unitPriceField = new BigDecimalField("Unit Price");
    final IntegerField reorderPointField = new IntegerField("Reorder Point");
    final Button saveButton = new Button("Save");
    final Button deleteButton = new Button("Delete");
    final Button addButton = new Button("Add product");
    final FormLayout form = new FormLayout();

    private Product currentProduct;

    public ProductsView(ProductService productService) {
        this.productService = productService;
        setSizeFull();
        setPadding(true);

        configureGrid();
        configureForm();

        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.setIcon(VaadinIcon.PLUS.create());
        addButton.addClickListener(e -> addProduct());

        VerticalLayout gridLayout = new VerticalLayout(toolbar, grid);
        gridLayout.setSizeFull();
        gridLayout.setPadding(false);

        VerticalLayout formLayout = new VerticalLayout(new H2("Product Details"), form);
        formLayout.setWidth("400px");
        formLayout.setPadding(false);

        HorizontalLayout content = new HorizontalLayout(gridLayout, formLayout);
        content.setSizeFull();
        content.setFlexGrow(1, gridLayout);

        add(content);
        setFormVisible(false);
        refreshGrid();
    }

    private void configureGrid() {
        grid.addColumn(Product::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Product::getSku).setHeader("SKU").setSortable(true);
        grid.addColumn(Product::getCategory).setHeader("Category").setSortable(true);
        grid.addColumn(p -> p.getUnitPrice() != null ? "$" + p.getUnitPrice() : "")
                .setHeader("Unit Price").setSortable(true)
                .setKey("unitPrice");
        grid.addColumn(Product::getReorderPoint).setHeader("Reorder Point").setSortable(true);
        grid.addColumn(Product::getCurrentStock).setHeader("Current Stock").setSortable(true);
        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(e -> editProduct(e.getValue()));
    }

    private void configureForm() {
        nameField.setRequiredIndicatorVisible(true);
        skuField.setRequiredIndicatorVisible(true);
        unitPriceField.setRequiredIndicatorVisible(true);
        reorderPointField.setValue(0);
        reorderPointField.setMin(0);
        reorderPointField.setStepButtonsVisible(true);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveProduct());

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> confirmDelete());

        Button cancelButton = new Button("Cancel", e -> cancelEdit());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, deleteButton, cancelButton);

        form.add(nameField, skuField, categoryField, unitPriceField, reorderPointField);
        form.add(buttons);
        form.setColspan(buttons, 2);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    }

    private void addProduct() {
        grid.asSingleSelect().clear();
        currentProduct = new Product();
        currentProduct.setReorderPoint(0);
        populateForm(currentProduct);
        setFormVisible(true);
        deleteButton.setVisible(false);
        nameField.focus();
    }

    private void editProduct(Product product) {
        if (product == null) {
            cancelEdit();
            return;
        }
        currentProduct = product;
        populateForm(product);
        setFormVisible(true);
        deleteButton.setVisible(true);
    }

    private void populateForm(Product product) {
        nameField.setValue(product.getName() != null ? product.getName() : "");
        skuField.setValue(product.getSku() != null ? product.getSku() : "");
        categoryField.setValue(product.getCategory() != null ? product.getCategory() : "");
        unitPriceField.setValue(product.getUnitPrice());
        reorderPointField.setValue(product.getReorderPoint());
    }

    private void saveProduct() {
        if (!validateForm()) {
            return;
        }

        currentProduct.setName(nameField.getValue().trim());
        currentProduct.setSku(skuField.getValue().trim());
        currentProduct.setCategory(categoryField.getValue().trim());
        currentProduct.setUnitPrice(unitPriceField.getValue());
        currentProduct.setReorderPoint(
                reorderPointField.getValue() != null ? reorderPointField.getValue() : 0);

        try {
            productService.save(currentProduct);
            Notification notification = Notification.show(
                    "Product saved: " + currentProduct.getName(), 3000,
                    Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refreshGrid();
            cancelEdit();
        } catch (Exception e) {
            Notification notification = Notification.show(
                    "Error saving product: " + e.getMessage(), 0,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (nameField.getValue() == null || nameField.getValue().trim().isEmpty()) {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Name is required");
            valid = false;
        } else {
            nameField.setInvalid(false);
        }

        if (skuField.getValue() == null || skuField.getValue().trim().isEmpty()) {
            skuField.setInvalid(true);
            skuField.setErrorMessage("SKU is required");
            valid = false;
        } else if (productService.isSkuTaken(skuField.getValue().trim(), currentProduct.getId())) {
            skuField.setInvalid(true);
            skuField.setErrorMessage("SKU already exists");
            valid = false;
        } else {
            skuField.setInvalid(false);
        }

        if (unitPriceField.getValue() == null ||
            unitPriceField.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            unitPriceField.setInvalid(true);
            unitPriceField.setErrorMessage("Unit price must be a positive number");
            valid = false;
        } else {
            unitPriceField.setInvalid(false);
        }

        if (reorderPointField.getValue() != null && reorderPointField.getValue() < 0) {
            reorderPointField.setInvalid(true);
            reorderPointField.setErrorMessage("Reorder point must be non-negative");
            valid = false;
        } else {
            reorderPointField.setInvalid(false);
        }

        return valid;
    }

    private void confirmDelete() {
        if (currentProduct == null || currentProduct.getId() == null) {
            return;
        }

        if (productService.hasStockEvents(currentProduct)) {
            Notification notification = Notification.show(
                    "Cannot delete product with existing stock events", 0,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete \"" + currentProduct.getName() + "\"?");
        dialog.setText("Are you sure you want to delete this product?");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            productService.delete(currentProduct);
            Notification notification = Notification.show(
                    "Product deleted: " + currentProduct.getName(), 3000,
                    Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refreshGrid();
            cancelEdit();
        });
        dialog.open();
    }

    private void cancelEdit() {
        currentProduct = null;
        grid.asSingleSelect().clear();
        setFormVisible(false);
    }

    private void setFormVisible(boolean visible) {
        form.getParent().ifPresent(parent -> parent.setVisible(visible));
    }

    private void refreshGrid() {
        grid.setItems(productService.findAll());
    }
}
