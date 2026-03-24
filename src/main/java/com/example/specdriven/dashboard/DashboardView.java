package com.example.specdriven.dashboard;

import com.example.specdriven.product.Product;
import com.example.specdriven.product.ProductService;
import com.example.specdriven.stock.StockEvent;
import com.example.specdriven.stock.StockService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Route("")
@PageTitle("Dashboard — Stash.log")
@PermitAll
public class DashboardView extends VerticalLayout {

    private final ProductService productService;
    private final StockService stockService;

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DashboardView(ProductService productService, StockService stockService) {
        this.productService = productService;
        this.stockService = stockService;
        setPadding(true);
        setSpacing(true);

        List<Product> products = productService.findAll();

        add(createSummaryCards(products));
        add(createLowStockAlerts(products));
        add(createRecentActivity());
    }

    private FlexLayout createSummaryCards(List<Product> products) {
        long totalProducts = products.size();

        BigDecimal totalStockValue = products.stream()
                .map(p -> p.getUnitPrice().multiply(BigDecimal.valueOf(p.getCurrentStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long lowStockCount = products.stream()
                .filter(p -> p.getCurrentStock() > 0 && p.getCurrentStock() <= p.getReorderPoint())
                .count();

        long outOfStockCount = products.stream()
                .filter(p -> p.getCurrentStock() == 0)
                .count();

        FlexLayout cards = new FlexLayout();
        cards.setWidthFull();
        cards.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        cards.getStyle().set("gap", "var(--vaadin-gap-m)");

        cards.add(createCard("Total Products", String.valueOf(totalProducts)));
        cards.add(createCard("Total Stock Value", "$" + totalStockValue.toPlainString()));
        cards.add(createCard("Low-Stock Items", String.valueOf(lowStockCount)));
        cards.add(createCard("Out-of-Stock Items", String.valueOf(outOfStockCount)));

        return cards;
    }

    private Div createCard(String label, String value) {
        Div card = new Div();
        card.addClassName("summary-card");

        Paragraph labelP = new Paragraph(label);
        labelP.addClassName("card-label");

        Paragraph valueP = new Paragraph(value);
        valueP.addClassName("card-value");

        card.add(labelP, valueP);
        return card;
    }

    private VerticalLayout createLowStockAlerts(List<Product> products) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);

        H2 title = new H2("Low-Stock Alerts");
        title.getStyle().set("font-size", "var(--aura-font-size-l)")
                .set("margin", "var(--vaadin-gap-m) 0 var(--vaadin-gap-s) 0");

        List<Product> lowStockProducts = products.stream()
                .filter(p -> p.getCurrentStock() >= 0 && p.getCurrentStock() <= p.getReorderPoint())
                .sorted(Comparator.comparingDouble(p ->
                        p.getReorderPoint() > 0
                                ? (double) p.getCurrentStock() / p.getReorderPoint()
                                : 0.0))
                .limit(10)
                .toList();

        Grid<Product> alertGrid = new Grid<>(Product.class, false);
        alertGrid.addColumn(Product::getName).setHeader("Name").setSortable(true);
        alertGrid.addColumn(Product::getSku).setHeader("SKU").setSortable(true);
        alertGrid.addColumn(Product::getCurrentStock).setHeader("Current Stock").setSortable(true);
        alertGrid.addColumn(Product::getReorderPoint).setHeader("Reorder Point").setSortable(true);
        alertGrid.setItems(lowStockProducts);
        alertGrid.setAllRowsVisible(true);

        section.add(title, alertGrid);
        return section;
    }

    private VerticalLayout createRecentActivity() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);

        H2 title = new H2("Recent Activity");
        title.getStyle().set("font-size", "var(--aura-font-size-l)")
                .set("margin", "var(--vaadin-gap-m) 0 var(--vaadin-gap-s) 0");

        List<StockEvent> recentEvents = stockService.findRecentEvents();

        Grid<StockEvent> activityGrid = new Grid<>(StockEvent.class, false);
        activityGrid.addColumn(e -> e.getTimestamp().format(TIMESTAMP_FORMAT))
                .setHeader("Timestamp").setSortable(true);
        activityGrid.addColumn(e -> e.getProduct().getName())
                .setHeader("Product").setSortable(true);
        activityGrid.addColumn(e -> e.getType().name())
                .setHeader("Event Type").setSortable(true);
        activityGrid.addColumn(StockEvent::getQuantity)
                .setHeader("Quantity").setSortable(true);
        activityGrid.addColumn(e -> e.getReason() != null ? e.getReason() : "")
                .setHeader("Reference/Reason").setSortable(true);
        activityGrid.setItems(recentEvents);
        activityGrid.setAllRowsVisible(true);

        section.add(title, activityGrid);
        return section;
    }
}
