package com.example.specdriven.purchase;

import com.example.specdriven.show.Show;
import com.example.specdriven.show.ShowService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Route("checkout")
@PageTitle("QuickTicket — Checkout")
public class CheckoutView extends VerticalLayout implements BeforeEnterObserver {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("EEE, MMM d yyyy 'at' h:mm a");

    private final ShowService showService;
    private final PurchaseService purchaseService;

    private Show show;
    private int quantity;

    private TextField cardNumberField;
    private TextField expiryField;
    private TextField cvvField;

    public CheckoutView(ShowService showService, PurchaseService purchaseService) {
        this.showService = showService;
        this.purchaseService = purchaseService;
        addClassName("checkout-view");
        setSizeFull();
        setPadding(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Map<String, List<String>> params =
                event.getLocation().getQueryParameters().getParameters();

        List<String> showIds = params.get("showId");
        List<String> quantities = params.get("quantity");

        if (showIds == null || showIds.isEmpty() || quantities == null || quantities.isEmpty()) {
            event.forwardTo("");
            return;
        }

        try {
            Long showId = Long.valueOf(showIds.get(0));
            this.quantity = Integer.parseInt(quantities.get(0));

            showService.findById(showId).ifPresentOrElse(
                    s -> {
                        this.show = s;
                        buildContent();
                    },
                    () -> event.forwardTo("")
            );
        } catch (NumberFormatException e) {
            event.forwardTo("");
        }
    }

    private void buildContent() {
        removeAll();

        H2 header = new H2("Checkout");
        header.addClassName("checkout-header");

        // Order summary
        Div summary = new Div();
        summary.addClassName("order-summary");

        H3 summaryTitle = new H3("Order Summary");
        Span showTitle = new Span(show.getTitle());
        showTitle.addClassName("summary-show-title");
        Span showDate = new Span(show.getDateTime().format(DATE_FMT));
        showDate.addClassName("summary-date");
        Span ticketInfo = new Span(
                String.format("%d ticket%s × $%.2f", quantity, quantity > 1 ? "s" : "", show.getPrice()));
        ticketInfo.addClassName("summary-tickets");

        BigDecimal total = show.getPrice().multiply(BigDecimal.valueOf(quantity));
        Span totalLabel = new Span(String.format("Total: $%.2f", total));
        totalLabel.addClassName("summary-total");

        summary.add(summaryTitle, showTitle, showDate, ticketInfo, totalLabel);

        // Credit card form
        Div form = new Div();
        form.addClassName("payment-form");

        H3 paymentTitle = new H3("Payment Details");

        cardNumberField = new TextField("Card Number");
        cardNumberField.setPlaceholder("1234 5678 9012 3456");
        cardNumberField.setMaxLength(19);
        cardNumberField.setWidthFull();

        expiryField = new TextField("Expiry Date");
        expiryField.setPlaceholder("MM/YY");
        expiryField.setMaxLength(5);

        cvvField = new TextField("CVV");
        cvvField.setPlaceholder("123");
        cvvField.setMaxLength(3);

        Div expiryAndCvv = new Div();
        expiryAndCvv.addClassName("expiry-cvv-row");
        expiryAndCvv.add(expiryField, cvvField);

        Button payButton = new Button("Pay $" + String.format("%.2f", total), e -> handlePayment());
        payButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        payButton.setWidthFull();
        payButton.addClassName("pay-button");

        form.add(paymentTitle, cardNumberField, expiryAndCvv, payButton);

        Button backButton = new Button("← Back to Show",
                e -> getUI().ifPresent(ui -> ui.navigate("show/" + show.getId())));
        backButton.addClassName("back-button");

        add(backButton, header, summary, form);
    }

    private void handlePayment() {
        if (!validateForm()) {
            return;
        }

        String cardNum = cardNumberField.getValue().replaceAll("\\s", "");
        String lastFour = cardNum.substring(cardNum.length() - 4);

        // Re-fetch show to get current seat count
        Show freshShow = showService.findById(show.getId()).orElse(null);
        if (freshShow == null || freshShow.getAvailableSeats() < quantity) {
            Notification.show("Not enough seats available.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Purchase purchase = purchaseService.createPurchase(freshShow, quantity, lastFour);
        getUI().ifPresent(ui -> ui.navigate("ticket/" + purchase.getConfirmationCode()));
    }

    private boolean validateForm() {
        boolean valid = true;

        // Card number: 16 digits (spaces allowed in input)
        String cardNum = cardNumberField.getValue().replaceAll("\\s", "");
        if (!cardNum.matches("\\d{16}")) {
            cardNumberField.setInvalid(true);
            cardNumberField.setErrorMessage("Card number must be 16 digits");
            valid = false;
        } else {
            cardNumberField.setInvalid(false);
        }

        // Expiry: MM/YY, not in the past
        String expiry = expiryField.getValue().trim();
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            expiryField.setInvalid(true);
            expiryField.setErrorMessage("Use MM/YY format");
            valid = false;
        } else {
            int month = Integer.parseInt(expiry.substring(0, 2));
            int year = 2000 + Integer.parseInt(expiry.substring(3, 5));
            YearMonth cardExpiry = YearMonth.of(year, month);
            if (cardExpiry.isBefore(YearMonth.now())) {
                expiryField.setInvalid(true);
                expiryField.setErrorMessage("Card is expired");
                valid = false;
            } else {
                expiryField.setInvalid(false);
            }
        }

        // CVV: 3 digits
        String cvv = cvvField.getValue().trim();
        if (!cvv.matches("\\d{3}")) {
            cvvField.setInvalid(true);
            cvvField.setErrorMessage("CVV must be 3 digits");
            valid = false;
        } else {
            cvvField.setInvalid(false);
        }

        return valid;
    }
}
