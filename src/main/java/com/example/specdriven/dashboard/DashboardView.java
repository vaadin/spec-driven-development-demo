package com.example.specdriven.dashboard;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("")
@PageTitle("Dashboard — Stash.log")
@PermitAll
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        add(new H1("Dashboard"));
        add(new com.vaadin.flow.component.html.Paragraph("Coming soon..."));
    }
}
