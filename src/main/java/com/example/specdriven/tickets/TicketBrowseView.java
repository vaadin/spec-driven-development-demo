package com.example.specdriven.tickets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("")
@PageTitle("Transit Tickets")
public class TicketBrowseView extends VerticalLayout {

    private final TicketService ticketService;
    private final Div ticketGrid = new Div();
    private NativeButton activeFilterBtn;

    public TicketBrowseView(TicketService ticketService) {
        this.ticketService = ticketService;

        setPadding(false);
        setSpacing(false);

        Div container = new Div();
        container.addClassName("page-container");

        // Header
        Div header = new Div();
        header.addClassName("browse-header");

        H1 title = new H1("Transit Tickets");
        title.addClassName("browse-title");

        Paragraph subtitle = new Paragraph("Find and purchase your ride");
        subtitle.addClassName("browse-subtitle");

        header.add(title, subtitle);
        container.add(header);

        // Filter bar
        container.add(createFilterBar());

        // Ticket grid
        ticketGrid.addClassName("ticket-grid");
        container.add(ticketGrid);

        add(container);

        showTickets(ticketService.findAll());
    }

    private Div createFilterBar() {
        Div bar = new Div();
        bar.addClassName("filter-bar");

        NativeButton allBtn = createFilterButton("All", null);
        allBtn.addClassName("active");
        activeFilterBtn = allBtn;
        bar.add(allBtn);

        for (TransitMode mode : TransitMode.values()) {
            bar.add(createFilterButton(getModeLabel(mode), mode));
        }

        return bar;
    }

    private NativeButton createFilterButton(String label, TransitMode mode) {
        NativeButton btn = new NativeButton(label);
        btn.addClassName("filter-btn");
        btn.addClickListener(e -> {
            if (activeFilterBtn != null) {
                activeFilterBtn.removeClassName("active");
            }
            btn.addClassName("active");
            activeFilterBtn = btn;

            if (mode == null) {
                showTickets(ticketService.findAll());
            } else {
                showTickets(ticketService.findByTransitMode(mode));
            }
        });
        return btn;
    }

    private void showTickets(List<Ticket> tickets) {
        ticketGrid.removeAll();
        for (Ticket ticket : tickets) {
            ticketGrid.add(createTicketCard(ticket));
        }
    }

    private Div createTicketCard(Ticket ticket) {
        Div card = new Div();
        card.addClassNames("ticket-card", getModeClass(ticket.getTransitMode()));

        // Emoji icon
        Div icon = new Div();
        icon.addClassName("ticket-card-icon");
        icon.setText(getEmoji(ticket.getTransitMode()));

        // Name
        Div name = new Div();
        name.addClassName("ticket-card-name");
        name.setText(ticket.getName());

        // Badges
        Span modeBadge = new Span(getModeLabel(ticket.getTransitMode()));
        modeBadge.addClassNames("badge", "badge-mode", ticket.getTransitMode().name().toLowerCase());

        Span typeBadge = new Span(getTypeLabel(ticket.getTicketType()));
        typeBadge.addClassNames("badge", "badge-type");

        Div badges = new Div();
        badges.add(modeBadge, typeBadge);

        // Price
        Div price = new Div();
        price.addClassNames("ticket-card-price", getModeClass(ticket.getTransitMode()));
        price.setText(String.format("$%.2f", ticket.getPrice()));

        card.add(icon, name, badges, price);
        card.addClickListener(e -> UI.getCurrent().navigate(TicketDetailView.class, ticket.getId()));

        return card;
    }

    private String getEmoji(TransitMode mode) {
        return switch (mode) {
            case BUS -> "\uD83D\uDE8C";
            case TRAIN -> "\uD83D\uDE86";
            case METRO -> "\uD83D\uDE87";
            case FERRY -> "\u26F4\uFE0F";
        };
    }

    private String getModeClass(TransitMode mode) {
        return "mode-" + mode.name().toLowerCase();
    }

    private String getModeLabel(TransitMode mode) {
        return mode.name().charAt(0) + mode.name().substring(1).toLowerCase();
    }

    private String getTypeLabel(TicketType type) {
        return type == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";
    }
}
