package com.example.specdriven.tickets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
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
        setPadding(true);
        setSpacing(true);
        setMaxWidth("600px");
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

        Button backBtn = new Button("Back to Details",
                e -> UI.getCurrent().navigate("ticket/" + ticket.getId()));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        add(backBtn);

        add(new H1("Checkout"));

        // Order summary
        Div summary = new Div();
        summary.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "8px")
                .set("padding", "16px")
                .set("margin-bottom", "16px");

        String modeLabel = ticket.getTransitMode().name().charAt(0)
                + ticket.getTransitMode().name().substring(1).toLowerCase();
        String typeLabel = ticket.getTicketType() == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";

        summary.add(new H3("Order Summary"));
        summary.add(new Paragraph("Ticket: " + ticket.getName()));
        summary.add(new Paragraph("Mode: " + modeLabel + " | Type: " + typeLabel));
        summary.add(new Paragraph("Quantity: " + quantity));
        summary.add(new Paragraph(String.format("Unit Price: $%.2f", ticket.getPrice())));

        BigDecimal total = ticket.getPrice().multiply(BigDecimal.valueOf(quantity));
        Paragraph totalP = new Paragraph(String.format("Total: $%.2f", total));
        totalP.getStyle().set("font-weight", "bold").set("font-size", "var(--lumo-font-size-xl)");
        summary.add(totalP);
        add(summary);

        // Credit card form
        add(new H3("Payment Details"));

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
        cvvField.setWidth("120px");

        add(nameField, cardNumberField, expiryField, cvvField);

        Button purchaseBtn = new Button("Purchase");
        purchaseBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        purchaseBtn.setEnabled(false);
        purchaseBtn.getStyle().set("margin-top", "16px");
        add(purchaseBtn);

        // Validation: enable button when all fields valid
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
            UI.getCurrent().navigate("confirmation/" + order.getConfirmationCode().toString());
        });
    }
}
