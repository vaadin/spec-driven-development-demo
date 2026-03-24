package com.example.specdriven.admin.queue;

import com.example.specdriven.model.*;
import com.example.specdriven.service.TicketService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.example.specdriven.admin.AdminLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "admin/queue", layout = AdminLayout.class)
@PageTitle("Ticket Queue | re:solve")
@RolesAllowed("ADMIN")
public class TicketQueueView extends VerticalLayout {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TicketService ticketService;
    private final Grid<Ticket> grid = new Grid<>();

    private Select<Status> statusFilter;
    private Select<Category> categoryFilter;
    private Select<Priority> priorityFilter;

    public TicketQueueView(TicketService ticketService) {
        this.ticketService = ticketService;

        setPadding(true);
        setSpacing(true);
        getStyle().set("max-width", "1200px").set("margin", "0 auto");

        add(new H1("Ticket Queue"));
        add(createFilterBar());
        configureGrid();
        add(grid);

        loadTickets();
    }

    private HorizontalLayout createFilterBar() {
        statusFilter = new Select<>();
        statusFilter.setLabel("Status");
        statusFilter.setItems(Status.values());
        statusFilter.setPlaceholder("All");
        statusFilter.setEmptySelectionAllowed(true);
        statusFilter.setEmptySelectionCaption("All");
        statusFilter.addValueChangeListener(e -> loadTickets());

        categoryFilter = new Select<>();
        categoryFilter.setLabel("Category");
        categoryFilter.setItems(Category.values());
        categoryFilter.setEmptySelectionAllowed(true);
        categoryFilter.setEmptySelectionCaption("All");
        categoryFilter.addValueChangeListener(e -> loadTickets());

        priorityFilter = new Select<>();
        priorityFilter.setLabel("Priority");
        priorityFilter.setItems(Priority.values());
        priorityFilter.setEmptySelectionAllowed(true);
        priorityFilter.setEmptySelectionCaption("All");
        priorityFilter.addValueChangeListener(e -> loadTickets());

        HorizontalLayout filters = new HorizontalLayout(statusFilter, categoryFilter, priorityFilter);
        filters.setAlignItems(Alignment.BASELINE);
        return filters;
    }

    private void configureGrid() {
        grid.addColumn(Ticket::getId).setHeader("ID").setSortable(true).setWidth("70px").setFlexGrow(0);
        grid.addColumn(Ticket::getTitle).setHeader("Title").setSortable(true).setFlexGrow(2);
        grid.addColumn(Ticket::getCategory).setHeader("Category").setSortable(true).setWidth("120px").setFlexGrow(0);

        grid.addComponentColumn(ticket -> {
            Span badge = new Span(ticket.getPriority().name());
            badge.getElement().getThemeList().add("badge");
            String cssClass = "priority-" + ticket.getPriority().name().toLowerCase();
            badge.addClassName(cssClass);
            return badge;
        }).setHeader("Priority").setSortable(true).setComparator(t -> t.getPriority().ordinal()).setWidth("100px").setFlexGrow(0);

        grid.addComponentColumn(ticket -> {
            Span badge = new Span(ticket.getStatus().name().replace("_", " "));
            badge.getElement().getThemeList().add("badge");
            String cssClass = "status-" + ticket.getStatus().name().toLowerCase().replace("_", "-");
            badge.addClassName(cssClass);
            return badge;
        }).setHeader("Status").setSortable(true).setComparator(t -> t.getStatus().ordinal()).setWidth("120px").setFlexGrow(0);

        grid.addColumn(t -> t.getCreatedBy() != null ? t.getCreatedBy().getName() : "")
                .setHeader("Created By").setSortable(true).setWidth("140px").setFlexGrow(0);

        grid.addColumn(t -> t.getAssignedTo() != null ? t.getAssignedTo().getName() : "Unassigned")
                .setHeader("Assigned To").setSortable(true).setWidth("140px").setFlexGrow(0);

        grid.addColumn(t -> t.getCreatedDate() != null ? t.getCreatedDate().format(FORMATTER) : "")
                .setHeader("Created").setSortable(true).setComparator(Ticket::getCreatedDate).setWidth("150px").setFlexGrow(0);

        grid.addComponentColumn(ticket -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button assignBtn = new Button("Assign to me", e -> {
                ticketService.assignTicketToMe(ticket.getId());
                Notification.show("Ticket assigned to you", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadTickets();
            });
            assignBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

            Anchor viewLink = new Anchor("admin/ticket/" + ticket.getId(), "View");

            actions.add(viewLink, assignBtn);
            return actions;
        }).setHeader("Actions").setWidth("180px").setFlexGrow(0);

        grid.setWidthFull();
        grid.setAllRowsVisible(true);
    }

    private void loadTickets() {
        List<Ticket> tickets;
        Status status = statusFilter.getValue();

        if (status != null) {
            tickets = ticketService.getTicketsByStatuses(List.of(status));
        } else {
            // Default: exclude CLOSED
            tickets = ticketService.getTicketsByStatuses(List.of(Status.OPEN, Status.IN_PROGRESS, Status.RESOLVED));
        }

        Category category = categoryFilter.getValue();
        if (category != null) {
            tickets = tickets.stream().filter(t -> t.getCategory() == category).toList();
        }

        Priority priority = priorityFilter.getValue();
        if (priority != null) {
            tickets = tickets.stream().filter(t -> t.getPriority() == priority).toList();
        }

        grid.setItems(tickets);
    }
}
