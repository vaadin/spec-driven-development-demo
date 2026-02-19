package com.example.specdriven.tickets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
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
        setPadding(false);
        setSpacing(false);
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

        Div container = new Div();
        container.addClassNames("page-container", "confirmation-container");

        // Success header
        Div successIcon = new Div();
        successIcon.addClassName("success-icon");
        successIcon.setText("\u2713");

        H1 heading = new H1("Purchase Successful!");
        heading.addClassName("success-heading");

        Paragraph subtitle = new Paragraph("Show this code when boarding");
        subtitle.addClassName("success-subtitle");

        container.add(successIcon, heading, subtitle);

        // Confirmation code block
        Div codeBox = new Div();
        codeBox.addClassName("confirmation-code-box");

        Div codeLabel = new Div();
        codeLabel.addClassName("confirmation-code-label");
        codeLabel.setText("CONFIRMATION CODE");

        Div codeValue = new Div();
        codeValue.addClassName("confirmation-code-value");
        codeValue.setText(order.getConfirmationCode().toString());

        codeBox.add(codeLabel, codeValue);
        container.add(codeBox);

        // Details list
        Ticket ticket = order.getTicket();
        String modeLabel = getModeLabel(ticket.getTransitMode());
        String typeLabel = getTypeLabel(ticket.getTicketType());

        Div detailsList = new Div();
        detailsList.addClassName("details-list");

        detailsList.add(createDetailRow("Ticket", ticket.getName()));
        detailsList.add(createDetailRow("Mode", modeLabel + " \u00B7 " + typeLabel));
        detailsList.add(createDetailRow("Quantity", String.valueOf(order.getQuantity())));
        detailsList.add(createDetailRow("Total", String.format("$%.2f", order.getTotalPrice())));
        detailsList.add(createDetailRow("Card", "**** " + order.getCardLastFour()));
        detailsList.add(createDetailRow("Purchased",
                order.getPurchasedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))));

        container.add(detailsList);

        // Secondary button
        NativeButton buyAnother = new NativeButton("Buy Another Ticket");
        buyAnother.addClassName("secondary-btn");
        buyAnother.getStyle().set("margin-top", "24px");
        buyAnother.addClickListener(e -> UI.getCurrent().navigate(TicketBrowseView.class));
        container.add(buyAnother);

        add(container);
    }

    private Div createDetailRow(String label, String value) {
        Div row = new Div();
        row.addClassName("details-row");

        Span rowLabel = new Span(label);
        rowLabel.addClassName("details-row-label");

        Span rowValue = new Span(value);
        rowValue.addClassName("details-row-value");

        row.add(rowLabel, rowValue);
        return row;
    }

    private String getModeLabel(TransitMode mode) {
        return mode.name().charAt(0) + mode.name().substring(1).toLowerCase();
    }

    private String getTypeLabel(TicketType type) {
        return type == TicketType.SINGLE_RIDE ? "Single Ride" : "Day Pass";
    }
}
