package com.example.specdriven.tickets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Route("confirmation")
@PageTitle("Purchase Confirmed")
public class TicketConfirmationView extends VerticalLayout implements HasUrlParameter<String> {

    private final PurchaseService purchaseService;

    public TicketConfirmationView(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
        setPadding(true);
        setSpacing(true);
        setMaxWidth("600px");
    }

    @Override
    public void setParameter(BeforeEvent event, String confirmationCode) {
        UUID uuid;
        try {
            uuid = UUID.fromString(confirmationCode);
        } catch (Exception e) {
            event.forwardTo("");
            return;
        }

        purchaseService.findByConfirmationCode(uuid).ifPresentOrElse(
                this::buildView,
                () -> event.forwardTo("")
        );
    }

    private void buildView(PurchaseOrder order) {
        removeAll();

        H1 success = new H1("Purchase Successful!");
        success.getStyle().set("color", "var(--lumo-success-text-color)");
        add(success);

        // Confirmation code - prominent
        Div codeBox = new Div();
        codeBox.getStyle()
                .set("background", "var(--lumo-primary-color-10pct)")
                .set("border", "2px solid var(--lumo-primary-color)")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("text-align", "center")
                .set("margin", "16px 0");

        Paragraph codeLabel = new Paragraph("Confirmation Code");
        codeLabel.getStyle()
                .set("margin", "0 0 8px 0")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)");

        H2 codeValue = new H2(order.getConfirmationCode().toString());
        codeValue.getStyle()
                .set("margin", "0")
                .set("word-break", "break-all");

        codeBox.add(codeLabel, codeValue);
        add(codeBox);

        // Ticket details
        Ticket ticket = order.getTicket();
        String modeLabel = ticket.getTransitMode().name().charAt(0)
                + ticket.getTransitMode().name().substring(1).toLowerCase();
        String typeLabel = ticket.getTicketType() == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";

        Div details = new Div();
        details.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "8px")
                .set("padding", "16px");

        details.add(new Paragraph("Ticket: " + ticket.getName()));
        details.add(new Paragraph("Mode: " + modeLabel + " | Type: " + typeLabel));
        details.add(new Paragraph("Quantity: " + order.getQuantity()));
        details.add(new Paragraph(String.format("Total: $%.2f", order.getTotalPrice())));
        details.add(new Paragraph("Card: **** **** **** " + order.getCardLastFour()));
        details.add(new Paragraph("Purchased: "
                + order.getPurchasedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))));
        add(details);

        Button buyAnother = new Button("Buy Another Ticket",
                e -> UI.getCurrent().navigate(""));
        buyAnother.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buyAnother.getStyle().set("margin-top", "24px");
        add(buyAnother);
    }
}
