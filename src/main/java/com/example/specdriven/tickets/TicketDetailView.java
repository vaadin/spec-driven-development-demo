package com.example.specdriven.tickets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;

@Route("ticket")
@PageTitle("Ticket Details")
public class TicketDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final TicketService ticketService;

    public TicketDetailView(TicketService ticketService) {
        this.ticketService = ticketService;
        setPadding(true);
        setSpacing(true);
        setMaxWidth("600px");
    }

    @Override
    public void setParameter(BeforeEvent event, Long ticketId) {
        ticketService.findById(ticketId).ifPresentOrElse(
                this::buildView,
                () -> event.forwardTo("")
        );
    }

    private void buildView(Ticket ticket) {
        removeAll();

        Button backBtn = new Button("Back to Browse", e -> UI.getCurrent().navigate(""));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        add(backBtn);

        add(new H1(ticket.getName()));

        String modeLabel = ticket.getTransitMode().name().charAt(0)
                + ticket.getTransitMode().name().substring(1).toLowerCase();
        String typeLabel = ticket.getTicketType() == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";

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
        add(badges);

        add(new Paragraph(ticket.getDescription()));

        H3 priceLabel = new H3(String.format("$%.2f per ticket", ticket.getPrice()));
        priceLabel.getStyle().set("margin-top", "16px");
        add(priceLabel);

        IntegerField quantityField = new IntegerField("Quantity");
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setMax(5);
        quantityField.setStepButtonsVisible(true);
        quantityField.setWidth("150px");
        add(quantityField);

        Paragraph subtotal = new Paragraph();
        subtotal.getStyle()
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "bold");
        updateSubtotal(subtotal, ticket.getPrice(), 1);
        add(subtotal);

        quantityField.addValueChangeListener(e -> {
            int qty = e.getValue() != null ? e.getValue() : 1;
            updateSubtotal(subtotal, ticket.getPrice(), qty);
        });

        Button checkoutBtn = new Button("Continue to Checkout",
                e -> {
                    int qty = quantityField.getValue() != null ? quantityField.getValue() : 1;
                    UI.getCurrent().navigate("checkout/" + ticket.getId() + "/" + qty);
                });
        checkoutBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        checkoutBtn.getStyle().set("margin-top", "16px");
        add(checkoutBtn);
    }

    private void updateSubtotal(Paragraph subtotal, BigDecimal price, int quantity) {
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        subtotal.setText(String.format("Subtotal: $%.2f", total));
    }
}
