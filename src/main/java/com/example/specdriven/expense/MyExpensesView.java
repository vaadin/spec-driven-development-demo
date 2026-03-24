package com.example.specdriven.expense;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("my-expenses")
@PageTitle("My Expenses — GreenLedger")
@RolesAllowed("EMPLOYEE")
public class MyExpensesView extends VerticalLayout {

    public MyExpensesView(ExpenseService expenseService) {
        setPadding(true);
        setSpacing(true);

        H2 heading = new H2("My Expenses");

        // Placeholder - will be fully implemented in UC-002
        Span placeholder = new Span("Your expenses will appear here.");

        add(heading, placeholder);
    }
}
