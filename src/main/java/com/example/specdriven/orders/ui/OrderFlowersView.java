package com.example.specdriven.orders.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.specdriven.orders.domain.Flower;
import com.example.specdriven.orders.domain.FlowerCatalog;
import com.example.specdriven.orders.service.OrderLineRequest;
import com.example.specdriven.orders.service.OrderService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Order Flowers")
public class OrderFlowersView extends VerticalLayout {

    private final OrderService orderService;
    private final List<CellField> cellFields = new ArrayList<>();
    private final TextField nameField = new TextField("Name");
    private final TextField phoneField = new TextField("Phone number");

    public OrderFlowersView(FlowerCatalog catalog, OrderService orderService) {
        this.orderService = orderService;

        setMaxWidth("720px");
        getStyle().set("margin", "0 auto");

        add(new H2("Order Flowers"));
        add(new Paragraph("Pick the flowers and colors you want, then enter your contact details."));

        for (Flower flower : catalog.getFlowers()) {
            add(buildFlowerRow(flower));
        }

        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();
        nameField.setId("name");
        phoneField.setRequiredIndicatorVisible(true);
        phoneField.setWidthFull();
        phoneField.setId("phone");
        add(nameField, phoneField);

        Button confirm = new Button("Confirm order", e -> submit());
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirm.setId("confirm-order");
        add(confirm);
    }

    private VerticalLayout buildFlowerRow(Flower flower) {
        VerticalLayout row = new VerticalLayout();
        row.setPadding(false);
        row.setSpacing(false);
        row.add(new H3(flower.name()));

        HorizontalLayout colorFields = new HorizontalLayout();
        colorFields.setWidthFull();
        for (String color : flower.colors()) {
            IntegerField qty = new IntegerField(color);
            qty.setMin(0);
            qty.setValue(0);
            qty.setStepButtonsVisible(true);
            qty.setId("qty-" + slug(flower.name()) + "-" + slug(color));
            colorFields.add(qty);
            colorFields.setFlexGrow(1, qty);
            cellFields.add(new CellField(flower.name(), color, qty));
        }
        colorFields.setAlignItems(FlexComponent.Alignment.END);
        row.add(colorFields);
        return row;
    }

    private void submit() {
        boolean nameMissing = nameField.getValue() == null || nameField.getValue().isBlank();
        boolean phoneMissing = phoneField.getValue() == null || phoneField.getValue().isBlank();

        nameField.setInvalid(nameMissing);
        nameField.setErrorMessage(nameMissing ? "Name is required" : null);
        phoneField.setInvalid(phoneMissing);
        phoneField.setErrorMessage(phoneMissing ? "Phone number is required" : null);

        List<OrderLineRequest> lines = cellFields.stream()
                .filter(cf -> cf.field.getValue() != null && cf.field.getValue() > 0)
                .map(cf -> new OrderLineRequest(cf.flower, cf.color, cf.field.getValue()))
                .toList();

        if (lines.isEmpty()) {
            Notification notification = Notification.show("Select at least one flower", 3000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        if (nameMissing || phoneMissing || lines.isEmpty()) {
            return;
        }

        orderService.placeOrder(nameField.getValue(), phoneField.getValue(), lines);
        UI.getCurrent().navigate(OrderConfirmationView.class);
    }

    private static String slug(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }

    private record CellField(String flower, String color, IntegerField field) {
    }
}
