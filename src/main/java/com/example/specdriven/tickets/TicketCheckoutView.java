package com.example.specdriven.tickets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;

@Route("checkout/:ticketId/:quantity")
@PageTitle("Checkout")
public class TicketCheckoutView extends VerticalLayout implements BeforeEnterObserver {

    private final TicketService ticketService;
    private final PurchaseService purchaseService;

    private Long ticketId;
    private int quantity;

    public TicketCheckoutView(TicketService ticketService, PurchaseService purchaseService) {
        this.ticketService = ticketService;
        this.purchaseService = purchaseService;
        setPadding(false);
        setSpacing(false);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            ticketId = event.getRouteParameters().getLong("ticketId").orElseThrow();
            quantity = event.getRouteParameters().getInteger("quantity").orElseThrow();
        } catch (Exception e) {
            event.forwardTo("");
            return;
        }

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
        NativeButton backLink = new NativeButton("\u2190 Back to Details");
        backLink.addClassName("back-link");
        backLink.addClickListener(e -> UI.getCurrent().navigate(TicketDetailView.class, ticket.getId()));
        container.add(backLink);

        // Page title
        H1 pageTitle = new H1("Checkout");
        pageTitle.addClassName("browse-title");
        container.add(pageTitle);

        // Checkout layout (2-panel on desktop)
        Div checkoutLayout = new Div();
        checkoutLayout.addClassName("checkout-layout");

        // Summary card (first in DOM for mobile order)
        Div summaryCard = buildSummaryCard(ticket);

        // Payment card
        Div paymentCard = buildPaymentCard(ticket);

        checkoutLayout.add(summaryCard, paymentCard);
        container.add(checkoutLayout);

        add(container);
    }

    private Div buildSummaryCard(Ticket ticket) {
        Div card = new Div();
        card.addClassNames("summary-card", "checkout-summary-sticky");

        Div header = new Div();
        header.addClassName("section-header");
        header.setText("ORDER SUMMARY");

        String modeLabel = getModeLabel(ticket.getTransitMode());
        String typeLabel = getTypeLabel(ticket.getTicketType());

        Div ticketName = new Div();
        ticketName.addClassName("summary-ticket-name");
        ticketName.setText(ticket.getName());

        Div subtitle = new Div();
        subtitle.addClassName("summary-ticket-subtitle");
        subtitle.setText(modeLabel + " \u00B7 " + typeLabel + " \u00B7 Qty: " + quantity);

        Div unitPrice = new Div();
        unitPrice.addClassName("summary-unit-price");
        unitPrice.setText(String.format("Unit price: $%.2f", ticket.getPrice()));

        BigDecimal total = ticket.getPrice().multiply(BigDecimal.valueOf(quantity));

        Hr divider = new Hr();
        divider.addClassName("summary-divider");

        Div totalRow = new Div();
        totalRow.addClassName("summary-row");

        Span totalLabel = new Span("Total");
        totalLabel.addClassName("summary-row-label");

        Span totalValue = new Span(String.format("$%.2f", total));
        totalValue.addClassName("summary-total");

        totalRow.add(totalLabel, totalValue);

        card.add(header, ticketName, subtitle, unitPrice, divider, totalRow);
        return card;
    }

    private Div buildPaymentCard(Ticket ticket) {
        Div card = new Div();
        card.addClassName("payment-card");

        Div header = new Div();
        header.addClassName("section-header");
        header.setText("PAYMENT DETAILS");

        TextField nameField = new TextField("Cardholder Name");
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextField cardNumberField = new TextField("Card Number");
        cardNumberField.setWidthFull();
        cardNumberField.setRequired(true);
        cardNumberField.setPlaceholder("1234567890123456");
        cardNumberField.setMaxLength(16);
        cardNumberField.setAllowedCharPattern("[0-9]");

        TextField expiryField = new TextField("Expiration (MM/YY)");
        expiryField.setPlaceholder("MM/YY");
        expiryField.setRequired(true);
        expiryField.setMaxLength(5);

        TextField cvvField = new TextField("CVV");
        cvvField.setRequired(true);
        cvvField.setPlaceholder("123");
        cvvField.setMaxLength(3);
        cvvField.setAllowedCharPattern("[0-9]");

        Div inlineRow = new Div();
        inlineRow.addClassName("payment-row-inline");
        inlineRow.add(expiryField, cvvField);

        Button purchaseBtn = new Button("Purchase");
        purchaseBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        purchaseBtn.setEnabled(false);
        purchaseBtn.setWidthFull();

        // Validation
        Runnable validate = () -> {
            boolean nameValid = !nameField.getValue().isBlank();
            boolean cardValid = cardNumberField.getValue().matches("\\d{16}");
            boolean expiryValid = expiryField.getValue().matches("(0[1-9]|1[0-2])/\\d{2}");
            boolean cvvValid = cvvField.getValue().matches("\\d{3}");
            purchaseBtn.setEnabled(nameValid && cardValid && expiryValid && cvvValid);
        };

        nameField.addValueChangeListener(e -> validate.run());
        cardNumberField.addValueChangeListener(e -> validate.run());
        expiryField.addValueChangeListener(e -> validate.run());
        cvvField.addValueChangeListener(e -> validate.run());

        purchaseBtn.addClickListener(e -> {
            String cardLastFour = cardNumberField.getValue()
                    .substring(cardNumberField.getValue().length() - 4);
            PurchaseOrder order = purchaseService.createPurchase(ticketId, quantity, cardLastFour);
            UI.getCurrent().navigate(TicketConfirmationView.class,
                    order.getConfirmationCode().toString());
        });

        card.add(header, nameField, cardNumberField, inlineRow, purchaseBtn);
        return card;
    }

    private String getModeLabel(TransitMode mode) {
        return mode.name().charAt(0) + mode.name().substring(1).toLowerCase();
    }

    private String getTypeLabel(TicketType type) {
        return type == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";
    }
}
