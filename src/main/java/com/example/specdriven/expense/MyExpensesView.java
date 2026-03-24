package com.example.specdriven.expense;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Route("my-expenses")
@PageTitle("My Expenses — GreenLedger")
@RolesAllowed("EMPLOYEE")
public class MyExpensesView extends VerticalLayout {

    private final ExpenseService expenseService;
    private final Grid<Expense> grid;
    private final Select<String> statusFilter;
    private final DatePicker fromDate;
    private final DatePicker toDate;
    private final Span totalLabel;

    public MyExpensesView(ExpenseService expenseService) {
        this.expenseService = expenseService;

        setPadding(true);
        setSpacing(true);

        H2 heading = new H2("My Expenses");

        // Filters
        statusFilter = new Select<>();
        statusFilter.setLabel("Status");
        statusFilter.setItems("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");
        statusFilter.addValueChangeListener(e -> refreshGrid());

        fromDate = new DatePicker("From");
        fromDate.addValueChangeListener(e -> refreshGrid());

        toDate = new DatePicker("To");
        toDate.addValueChangeListener(e -> refreshGrid());

        HorizontalLayout filters = new HorizontalLayout(statusFilter, fromDate, toDate);
        filters.setAlignItems(Alignment.BASELINE);

        // Grid
        grid = new Grid<>(Expense.class, false);
        grid.addColumn(Expense::getDate).setHeader("Date").setSortable(true).setKey("date");
        grid.addColumn(e -> e.getCategory().getDisplayName()).setHeader("Category").setSortable(true);
        grid.addColumn(Expense::getDescription).setHeader("Description");
        grid.addColumn(e -> String.format("$%.2f", e.getAmount())).setHeader("Amount").setSortable(true);
        grid.addComponentColumn(this::createStatusBadge).setHeader("Status").setSortable(true);

        grid.sort(List.of(new com.vaadin.flow.component.grid.GridSortOrder<>(
                grid.getColumnByKey("date"),
                com.vaadin.flow.data.provider.SortDirection.DESCENDING)));

        grid.setWidthFull();

        // Total
        totalLabel = new Span();
        totalLabel.getStyle().set("font-weight", "bold").set("font-size", "var(--aura-font-size-l)");

        add(heading, filters, grid, totalLabel);
        refreshGrid();
    }

    private void refreshGrid() {
        List<Expense> expenses = expenseService.getMyExpenses();

        // Filter by status
        String status = statusFilter.getValue();
        if (status != null && !"All".equals(status)) {
            ExpenseStatus filterStatus = ExpenseStatus.valueOf(status.toUpperCase());
            expenses = expenses.stream().filter(e -> e.getStatus() == filterStatus).toList();
        }

        // Filter by date range
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        if (from != null) {
            expenses = expenses.stream().filter(e -> !e.getDate().isBefore(from)).toList();
        }
        if (to != null) {
            expenses = expenses.stream().filter(e -> !e.getDate().isAfter(to)).toList();
        }

        // Sort by date descending
        expenses = expenses.stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .toList();

        grid.setItems(expenses);

        // Update total
        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private Span createStatusBadge(Expense expense) {
        Span badge = new Span(expense.getStatus().name());
        String cssClass = switch (expense.getStatus()) {
            case PENDING -> "badge-pending";
            case APPROVED -> "badge-approved";
            case REJECTED -> "badge-rejected";
        };
        badge.addClassName(cssClass);
        return badge;
    }
}
