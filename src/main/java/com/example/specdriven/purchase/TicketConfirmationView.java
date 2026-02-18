package com.example.specdriven.purchase;

import com.example.specdriven.show.Show;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;

@Route("ticket")
@PageTitle("QuickTicket — Your Ticket")
public class TicketConfirmationView extends VerticalLayout implements HasUrlParameter<String> {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("EEE, MMM d yyyy 'at' h:mm a");

    private final PurchaseService purchaseService;

    public TicketConfirmationView(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
        addClassName("confirmation-view");
        setSizeFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent event, String confirmationCode) {
        purchaseService.findByConfirmationCode(confirmationCode).ifPresentOrElse(
                this::buildContent,
                () -> {
                    add(new Paragraph("Ticket not found."));
                    Button back = new Button("Back to Shows",
                            e -> getUI().ifPresent(ui -> ui.navigate("")));
                    add(back);
                }
        );
    }

    private void buildContent(Purchase purchase) {
        removeAll();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Show show = purchase.getShow();

        Div ticket = new Div();
        ticket.addClassName("ticket-card");

        // Success header
        H2 header = new H2("Your Ticket");
        header.addClassName("ticket-header");

        Paragraph successMsg = new Paragraph("Payment successful! Show this code at the venue.");
        successMsg.addClassName("ticket-success-msg");

        // Confirmation code — displayed prominently
        Div codeSection = new Div();
        codeSection.addClassName("confirmation-code-section");
        Span codeLabel = new Span("Confirmation Code");
        codeLabel.addClassName("code-label");
        Span code = new Span(purchase.getConfirmationCode());
        code.addClassName("confirmation-code");
        codeSection.add(codeLabel, code);

        // Purchase details
        Div details = new Div();
        details.addClassName("ticket-details");

        Span showTitle = new Span(show.getTitle());
        showTitle.addClassName("ticket-show-title");
        Span showDate = new Span(show.getDateTime().format(DATE_FMT));
        showDate.addClassName("ticket-date");
        Span ticketCount = new Span(
                String.format("%d ticket%s", purchase.getQuantity(), purchase.getQuantity() > 1 ? "s" : ""));
        ticketCount.addClassName("ticket-count");
        Span totalPaid = new Span(String.format("Total paid: $%.2f", purchase.getTotalPrice()));
        totalPaid.addClassName("ticket-total");

        details.add(showTitle, showDate, ticketCount, totalPaid);

        ticket.add(header, successMsg, codeSection, details);

        Button browseMore = new Button("Browse More Shows",
                e -> getUI().ifPresent(ui -> ui.navigate("")));
        browseMore.addClassName("browse-more-button");

        add(ticket, browseMore);
    }
}
