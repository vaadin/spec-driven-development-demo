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
        cardGrid.getStyle().set("gap", "16px");
        cardGrid.setWidthFull();

        showTickets(ticketService.findAll());
    }

    private HorizontalLayout createFilterBar() {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setSpacing(true);
        bar.getStyle().set("flex-wrap", "wrap");

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
        card.getStyle()
                .set("flex", "1 1 300px")
                .set("max-width", "400px")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("cursor", "pointer")
                .set("transition", "box-shadow 0.2s");

        String modeLabel = ticket.getTransitMode().name().charAt(0)
                + ticket.getTransitMode().name().substring(1).toLowerCase();
        String typeLabel = ticket.getTicketType() == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";

        H3 name = new H3(ticket.getName());
        name.getStyle().set("margin", "0 0 8px 0");

        Span mode = new Span(modeLabel);
        mode.getStyle()
                .set("background", "var(--lumo-primary-color-10pct)")
                .set("color", "var(--lumo-primary-text-color)")
                .set("padding", "2px 8px")
                .set("border-radius", "4px")
                .set("font-size", "var(--lumo-font-size-s)");

        Span type = new Span(typeLabel);
        type.getStyle()
                .set("background", "var(--lumo-contrast-10pct)")
                .set("padding", "2px 8px")
                .set("border-radius", "4px")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("margin-left", "8px");

        HorizontalLayout badges = new HorizontalLayout(mode, type);
        badges.setSpacing(false);
        badges.getStyle().set("margin-bottom", "12px");

        Paragraph price = new Paragraph(String.format("$%.2f", ticket.getPrice()));
        price.getStyle()
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "bold")
                .set("margin", "0");

        card.add(name, badges, price);
        card.addClickListener(e -> UI.getCurrent().navigate("ticket/" + ticket.getId()));

        return card;
    }
}
