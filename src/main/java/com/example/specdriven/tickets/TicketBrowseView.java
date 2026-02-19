package com.example.specdriven.tickets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("")
@PageTitle("Transit Tickets")
public class TicketBrowseView extends VerticalLayout {

    private final TicketService ticketService;
    private final FlexLayout cardGrid = new FlexLayout();
    private Button activeFilter;

    public TicketBrowseView(TicketService ticketService) {
        this.ticketService = ticketService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H1("Transit Tickets"));
        add(createFilterBar());
        add(cardGrid);

        cardGrid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        cardGrid.addClassName("ticket-card-grid");
        cardGrid.setWidthFull();

        showTickets(ticketService.findAll());
    }

    private HorizontalLayout createFilterBar() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setSpacing(true);
        bar.addClassName("filter-bar");

        Button allBtn = createFilterButton("All", null);
        allBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        activeFilter = allBtn;
        bar.add(allBtn);

        for (TransitMode mode : TransitMode.values()) {
            String label = mode.name().charAt(0) + mode.name().substring(1).toLowerCase();
            bar.add(createFilterButton(label, mode));
        }

        return bar;
    }

    private Button createFilterButton(String label, TransitMode mode) {
        Button btn = new Button(label, e -> {
            if (activeFilter != null) {
                activeFilter.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
            e.getSource().addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            activeFilter = e.getSource();

            if (mode == null) {
                showTickets(ticketService.findAll());
            } else {
                showTickets(ticketService.findByTransitMode(mode));
            }
        });
        return btn;
    }

    private void showTickets(List<Ticket> tickets) {
        cardGrid.removeAll();
        for (Ticket ticket : tickets) {
            cardGrid.add(createTicketCard(ticket));
        }
    }

    private Div createTicketCard(Ticket ticket) {
        Div card = new Div();
        card.addClassName("ticket-card");

        String modeLabel = ticket.getTransitMode().name().charAt(0)
                + ticket.getTransitMode().name().substring(1).toLowerCase();
        String typeLabel = ticket.getTicketType() == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";

        H3 name = new H3(ticket.getName());
        name.addClassName("ticket-card-title");

        Span mode = new Span(modeLabel);
        mode.addClassNames("badge", "badge-mode");

        Span type = new Span(typeLabel);
        type.addClassNames("badge", "badge-type");

        HorizontalLayout badges = new HorizontalLayout(mode, type);
        badges.setSpacing(false);
        badges.addClassName("ticket-card-badges");

        Paragraph price = new Paragraph(String.format("$%.2f", ticket.getPrice()));
        price.addClassNames("price-text", "ticket-card-price");

        card.add(name, badges, price);
        card.addClickListener(e -> UI.getCurrent().navigate(TicketDetailView.class, ticket.getId()));

        return card;
    }
}
