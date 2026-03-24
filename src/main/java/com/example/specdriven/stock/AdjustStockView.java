package com.example.specdriven.stock;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("adjust")
@PageTitle("Adjust Stock — Stash.log")
@RolesAllowed("ADMIN")
public class AdjustStockView extends VerticalLayout {

    public AdjustStockView() {
        add(new H1("Adjust Stock"));
        add(new com.vaadin.flow.component.html.Paragraph("Coming soon..."));
    }
}
