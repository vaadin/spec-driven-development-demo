package com.example.specdriven.orders.ui;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("order/confirmation")
@PageTitle("Order confirmed")
public class OrderConfirmationView extends VerticalLayout {

    public OrderConfirmationView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H2 heading = new H2("Thank you for now");
        heading.setId("confirmation-heading");
        add(heading);
        add(new Paragraph("Your flower order has been received."));
    }
}
