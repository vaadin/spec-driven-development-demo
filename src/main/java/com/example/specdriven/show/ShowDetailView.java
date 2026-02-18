package com.example.specdriven.show;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Route("show")
@PageTitle("QuickTicket — Show Details")
public class ShowDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("EEE, MMM d yyyy 'at' h:mm a");

    private final ShowService showService;

    public ShowDetailView(ShowService showService) {
        this.showService = showService;
        addClassName("show-detail-view");
        setSizeFull();
        setPadding(true);
    }

    @Override
    public void setParameter(BeforeEvent event, Long showId) {
        showService.findById(showId).ifPresentOrElse(
                this::buildContent,
                () -> {
                    add(new Paragraph("Show not found."));
                    Button back = new Button("Back to Shows",
                            e -> getUI().ifPresent(ui -> ui.navigate("")));
                    add(back);
                }
        );
    }

    private void buildContent(Show show) {
        removeAll();

        Div layout = new Div();
        layout.addClassName("detail-layout");

        // Cover image
        Image image = new Image(show.getCoverImageUrl(), show.getTitle());
        image.addClassName("detail-image");

        // Info section
        Div info = new Div();
        info.addClassName("detail-info");

        H2 title = new H2(show.getTitle());
        Span dateTime = new Span(show.getDateTime().format(DATE_FMT));
        dateTime.addClassName("detail-date");
        Paragraph description = new Paragraph(show.getDescription());
        description.addClassName("detail-description");
        Span priceLabel = new Span(String.format("$%.2f per ticket", show.getPrice()));
        priceLabel.addClassName("detail-price");

        info.add(title, dateTime, description, priceLabel);

        // Ticket selector
        Div ticketSection = new Div();
        ticketSection.addClassName("ticket-section");

        int maxTickets = Math.min(6, show.getAvailableSeats());

        IntegerField quantityField = new IntegerField("Tickets");
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setMax(maxTickets);
        quantityField.setStepButtonsVisible(true);
        quantityField.addClassName("ticket-quantity");

        Span subtotal = new Span(formatSubtotal(show.getPrice(), 1));
        subtotal.addClassName("detail-subtotal");

        quantityField.addValueChangeListener(e -> {
            int qty = e.getValue() != null ? e.getValue() : 1;
            if (qty < 1) qty = 1;
            if (qty > maxTickets) qty = maxTickets;
            subtotal.setText(formatSubtotal(show.getPrice(), qty));
        });

        Button buyButton = new Button("Buy Tickets", e -> {
            int qty = quantityField.getValue() != null ? quantityField.getValue() : 1;
            getUI().ifPresent(ui ->
                    ui.navigate("checkout?showId=" + show.getId() + "&quantity=" + qty));
        });
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        buyButton.addClassName("buy-button");

        ticketSection.add(quantityField, subtotal, buyButton);
        info.add(ticketSection);

        layout.add(image, info);

        Button backButton = new Button("← Back to Shows",
                e -> getUI().ifPresent(ui -> ui.navigate("")));
        backButton.addClassName("back-button");

        add(backButton, layout);
    }

    private String formatSubtotal(BigDecimal price, int quantity) {
        return String.format("Subtotal: $%.2f", price.multiply(BigDecimal.valueOf(quantity)));
    }
}
