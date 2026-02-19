package com.example.specdriven.tickets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import java.math.BigDecimal;
import java.util.Map;

@Route("ticket")
@PageTitle("Ticket Details")
public class TicketDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final TicketService ticketService;

    public TicketDetailView(TicketService ticketService) {
        this.ticketService = ticketService;
        setPadding(false);
        setSpacing(false);
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

        Div container = new Div();
        container.addClassName("page-container");

        // Back link
        NativeButton backLink = new NativeButton("\u2190 Back to Browse");
        backLink.addClassName("back-link");
        backLink.addClickListener(e -> UI.getCurrent().navigate(TicketBrowseView.class));
        container.add(backLink);

        // Detail layout (2-panel on desktop)
        Div detailLayout = new Div();
        detailLayout.addClassName("detail-layout");

        // Left column: Ticket info card
        Div infoCard = new Div();
        infoCard.addClassName("detail-card");

        Div icon = new Div();
        icon.addClassName("ticket-card-icon");
        icon.setText(getEmoji(ticket.getTransitMode()));

        H2 title = new H2(ticket.getName());
        title.addClassName("detail-title");

        Span modeBadge = new Span(getModeLabel(ticket.getTransitMode()));
        modeBadge.addClassNames("badge", "badge-mode", ticket.getTransitMode().name().toLowerCase());

        Span typeBadge = new Span(getTypeLabel(ticket.getTicketType()));
        typeBadge.addClassNames("badge", "badge-type");

        Div badges = new Div();
        badges.add(modeBadge, typeBadge);

        Paragraph description = new Paragraph(ticket.getDescription());
        description.addClassName("detail-description");

        Div priceDiv = new Div();
        priceDiv.addClassName("detail-price");
        priceDiv.add(new Span(String.format("$%.2f ", ticket.getPrice())));
        Span unit = new Span("per ticket");
        unit.addClassName("detail-price-unit");
        priceDiv.add(unit);

        infoCard.add(icon, title, badges, description, priceDiv);

        // Right column: Purchase panel
        Div purchasePanel = new Div();
        purchasePanel.addClassName("purchase-panel");

        Div quantityLabel = new Div();
        quantityLabel.addClassName("quantity-label");
        quantityLabel.setText("Quantity");

        IntegerField quantityField = new IntegerField();
        quantityField.addClassName("quantity-stepper");
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setMax(5);
        quantityField.setStepButtonsVisible(true);

        // Subtotal box
        Div subtotalBox = new Div();
        subtotalBox.addClassName("subtotal-box");

        Div subtotalLabel = new Div();
        subtotalLabel.addClassName("subtotal-label");
        subtotalLabel.setText("SUBTOTAL");

        Div subtotalValue = new Div();
        subtotalValue.addClassName("subtotal-value");
        updateSubtotal(subtotalValue, ticket.getPrice(), 1);

        subtotalBox.add(subtotalLabel, subtotalValue);

        quantityField.addValueChangeListener(e -> {
            int qty = e.getValue() != null ? e.getValue() : 1;
            updateSubtotal(subtotalValue, ticket.getPrice(), qty);
        });

        Button checkoutBtn = new Button("Continue to Checkout",
                e -> {
                    int qty = quantityField.getValue() != null ? quantityField.getValue() : 1;
                    UI.getCurrent().navigate(TicketCheckoutView.class,
                            new RouteParameters(Map.of(
                                    "ticketId", String.valueOf(ticket.getId()),
                                    "quantity", String.valueOf(qty))));
                });
        checkoutBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        checkoutBtn.setWidthFull();

        purchasePanel.add(quantityLabel, quantityField, subtotalBox, checkoutBtn);

        detailLayout.add(infoCard, purchasePanel);
        container.add(detailLayout);

        add(container);
    }

    private void updateSubtotal(Div subtotalValue, BigDecimal price, int quantity) {
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        subtotalValue.setText(String.format("$%.2f", total));
    }

    private String getEmoji(TransitMode mode) {
        return switch (mode) {
            case BUS -> "\uD83D\uDE8C";
            case TRAIN -> "\uD83D\uDE86";
            case METRO -> "\uD83D\uDE87";
            case FERRY -> "\u26F4\uFE0F";
        };
    }

    private String getModeLabel(TransitMode mode) {
        return mode.name().charAt(0) + mode.name().substring(1).toLowerCase();
    }

    private String getTypeLabel(TicketType type) {
        return type == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";
    }
}
