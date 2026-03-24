package com.example.specdriven.expense;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("dashboard")
@PageTitle("Dashboard — GreenLedger")
@RolesAllowed("MANAGER")
public class DashboardView extends VerticalLayout {

    public DashboardView(ExpenseService expenseService) {
        setPadding(true);
        setSpacing(true);

        H2 heading = new H2("Dashboard");
        Span placeholder = new Span("Dashboard will appear here.");

        add(heading, placeholder);
    }
}
