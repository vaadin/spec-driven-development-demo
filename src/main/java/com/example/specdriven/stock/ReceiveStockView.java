package com.example.specdriven.stock;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("receive")
@PageTitle("Receive Stock — Stash.log")
@PermitAll
public class ReceiveStockView extends VerticalLayout {

    public ReceiveStockView() {
        add(new H1("Receive Stock"));
        add(new com.vaadin.flow.component.html.Paragraph("Coming soon..."));
    }
}
