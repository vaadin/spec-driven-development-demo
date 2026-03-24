package com.example.specdriven.inventory;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("inventory")
@PageTitle("Inventory — Stash.log")
@PermitAll
public class InventoryView extends VerticalLayout {

    public InventoryView() {
        add(new H1("Inventory"));
        add(new com.vaadin.flow.component.html.Paragraph("Coming soon..."));
    }
}
