package com.example.specdriven.inventory;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("inventory")
@PageTitle("Inventory — Stash.log")
@PermitAll
public class InventoryView extends VerticalLayout {

    private final ProductService productService;

    final Grid<Product> grid = new Grid<>(Product.class, false);
    final TextField searchField = new TextField();
    final ComboBox<String> categoryFilter = new ComboBox<>("Category");

    private GridListDataView<Product> dataView;

    public InventoryView(ProductService productService) {
        this.productService = productService;
        setSizeFull();
        setPadding(true);

        configureGrid();
        configureToolbar();

        dataView = grid.setItems(productService.findAll());

        add(createToolbar(), grid);
    }

    private void configureGrid() {
        grid.addColumn(Product::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Product::getSku).setHeader("SKU").setSortable(true);
        grid.addColumn(Product::getCategory).setHeader("Category").setSortable(true);
        grid.addColumn(p -> p.getUnitPrice() != null ? "$" + p.getUnitPrice() : "")
                .setHeader("Unit Price").setSortable(true).setKey("unitPrice");
        grid.addColumn(Product::getCurrentStock).setHeader("Current Stock").setSortable(true);
        grid.addComponentColumn(this::createStatusBadge).setHeader("Status").setSortable(true)
                .setKey("status")
                .setComparator((a, b) -> getStatusOrder(a) - getStatusOrder(b));
        grid.setSizeFull();
    }

    private void configureToolbar() {
        searchField.setPlaceholder("Search by name or SKU...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> applyFilters());

        categoryFilter.setPlaceholder("All categories");
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setItems(productService.findAllCategories());
        categoryFilter.addValueChangeListener(e -> applyFilters());
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(searchField, categoryFilter);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.BASELINE);
        return toolbar;
    }

    private void applyFilters() {
        dataView.setFilter(product -> {
            String search = searchField.getValue();
            String category = categoryFilter.getValue();

            boolean matchesSearch = search == null || search.isBlank()
                    || product.getName().toLowerCase().contains(search.toLowerCase())
                    || product.getSku().toLowerCase().contains(search.toLowerCase());

            boolean matchesCategory = category == null || category.isBlank()
                    || category.equals(product.getCategory());

            return matchesSearch && matchesCategory;
        });
    }

    private Span createStatusBadge(Product product) {
        Span badge = new Span();
        badge.addClassName("badge");

        if (product.getCurrentStock() == 0) {
            badge.setText("Out of Stock");
            badge.addClassName("badge-error");
        } else if (product.getCurrentStock() <= product.getReorderPoint()) {
            badge.setText("Low Stock");
            badge.addClassName("badge-warning");
        } else {
            badge.setText("In Stock");
            badge.addClassName("badge-success");
        }

        return badge;
    }

    private int getStatusOrder(Product product) {
        if (product.getCurrentStock() == 0) return 0;
        if (product.getCurrentStock() <= product.getReorderPoint()) return 1;
        return 2;
    }
}
