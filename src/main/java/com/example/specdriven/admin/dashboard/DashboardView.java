package com.example.specdriven.admin.dashboard;

import com.example.specdriven.model.Category;
import com.example.specdriven.model.Priority;
import com.example.specdriven.service.TicketService;
import com.example.specdriven.admin.AdminLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Map;

@Route(value = "admin/dashboard", layout = AdminLayout.class)
@PageTitle("Dashboard | re:solve")
@RolesAllowed("ADMIN")
public class DashboardView extends VerticalLayout {

    private final TicketService ticketService;

    public DashboardView(TicketService ticketService) {
        this.ticketService = ticketService;
        setPadding(true);
        setSpacing(true);
        getStyle().set("max-width", "1200px").set("margin", "0 auto");

        add(new H1("Dashboard"));
        add(buildStatCards());
        add(buildBreakdownTables());
    }

    private Div buildStatCards() {
        long openCount = ticketService.countByStatus(com.example.specdriven.model.Status.OPEN);
        long inProgressCount = ticketService.countByStatus(com.example.specdriven.model.Status.IN_PROGRESS);
        long resolvedToday = ticketService.countResolvedToday();
        long closedToday = ticketService.countClosedToday();

        Div container = new Div();
        container.addClassName("stat-cards");

        container.add(createStatCard("Open", openCount, "open"));
        container.add(createStatCard("In Progress", inProgressCount, "in-progress"));
        container.add(createStatCard("Resolved Today", resolvedToday, "resolved"));
        container.add(createStatCard("Closed Today", closedToday, "closed"));

        return container;
    }

    private Div createStatCard(String label, long value, String cssClass) {
        Div card = new Div();
        card.addClassNames("stat-card", cssClass);

        Div valueDiv = new Div();
        valueDiv.addClassName("stat-value");
        valueDiv.setText(String.valueOf(value));

        Div labelDiv = new Div();
        labelDiv.addClassName("stat-label");
        labelDiv.setText(label);

        card.add(valueDiv, labelDiv);
        return card;
    }

    private Div buildBreakdownTables() {
        Div container = new Div();
        container.addClassName("breakdown-tables");

        // By Category
        Div categoryCard = new Div();
        categoryCard.addClassName("breakdown-card");
        categoryCard.add(new H3("Tickets by Category"));

        Grid<Map.Entry<Category, Long>> categoryGrid = new Grid<>();
        categoryGrid.addColumn(e -> e.getKey().name()).setHeader("Category");
        categoryGrid.addColumn(Map.Entry::getValue).setHeader("Count");
        categoryGrid.setItems(ticketService.getOpenTicketsByCategory().entrySet());
        categoryGrid.setAllRowsVisible(true);
        categoryCard.add(categoryGrid);

        // By Priority
        Div priorityCard = new Div();
        priorityCard.addClassName("breakdown-card");
        priorityCard.add(new H3("Tickets by Priority"));

        Grid<Map.Entry<Priority, Long>> priorityGrid = new Grid<>();
        priorityGrid.addColumn(e -> e.getKey().name()).setHeader("Priority");
        priorityGrid.addColumn(Map.Entry::getValue).setHeader("Count");
        priorityGrid.setItems(ticketService.getOpenTicketsByPriority().entrySet());
        priorityGrid.setAllRowsVisible(true);
        priorityCard.add(priorityGrid);

        container.add(categoryCard, priorityCard);
        return container;
    }
}
