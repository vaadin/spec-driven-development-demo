package com.example.specdriven.expense;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.util.Comparator;

@Route("review-expenses")
@PageTitle("Review Expenses — GreenLedger")
@RolesAllowed("MANAGER")
public class ReviewExpensesView extends VerticalLayout {

    private final ExpenseService expenseService;
    private final Grid<Expense> grid;

    public ReviewExpensesView(ExpenseService expenseService) {
        this.expenseService = expenseService;

        setPadding(true);
        setSpacing(true);

        H2 heading = new H2("Review Expenses");

        grid = new Grid<>(Expense.class, false);
        grid.addColumn(Expense::getSubmittedBy).setHeader("Employee").setSortable(true);
        grid.addColumn(Expense::getDate).setHeader("Date").setSortable(true);
        grid.addColumn(e -> e.getCategory().getDisplayName()).setHeader("Category").setSortable(true);
        grid.addColumn(Expense::getDescription).setHeader("Description");
        grid.addColumn(e -> String.format("$%.2f", e.getAmount())).setHeader("Amount").setSortable(true);
        grid.addComponentColumn(e -> {
            Span indicator = new Span(e.hasReceipt() ? "Yes" : "No");
            return indicator;
        }).setHeader("Receipt");

        grid.addItemClickListener(e -> showExpenseDetails(e.getItem()));
        grid.setWidthFull();

        add(heading, grid);
        refreshGrid();
    }

    private void refreshGrid() {
        var expenses = expenseService.getPendingExpenses().stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .toList();
        grid.setItems(expenses);
    }

    private void showExpenseDetails(Expense expense) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Expense Details");
        dialog.setWidth("500px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(false);

        content.add(createDetailRow("Employee", expense.getSubmittedBy()));
        content.add(createDetailRow("Date", expense.getDate().toString()));
        content.add(createDetailRow("Category", expense.getCategory().getDisplayName()));
        content.add(createDetailRow("Description", expense.getDescription()));
        content.add(createDetailRow("Amount", String.format("$%.2f", expense.getAmount())));

        if (expense.hasReceipt()) {
            StreamResource resource = new StreamResource(expense.getReceiptFileName(),
                    () -> new ByteArrayInputStream(expense.getReceiptData()));
            resource.setContentType(expense.getReceiptContentType());
            Image receiptImage = new Image(resource, "Receipt");
            receiptImage.setMaxWidth("100%");
            receiptImage.setMaxHeight("300px");
            content.add(receiptImage);
        }

        // Rejection comment area (hidden initially)
        TextArea rejectionComment = new TextArea("Rejection Reason");
        rejectionComment.setWidthFull();
        rejectionComment.setVisible(false);

        Span rejectionError = new Span();
        rejectionError.getStyle().set("color", "var(--error)");
        rejectionError.setVisible(false);

        Button approveButton = new Button("Approve", e -> {
            expenseService.approveExpense(expense.getId());
            Notification.show("Expense approved", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.SUCCESS);
            dialog.close();
            refreshGrid();
        });
        approveButton.addThemeVariants(ButtonVariant.PRIMARY);

        Button rejectButton = new Button("Reject", e -> {
            if (!rejectionComment.isVisible()) {
                rejectionComment.setVisible(true);
                rejectionError.setVisible(false);
                return;
            }
            if (rejectionComment.getValue().isBlank()) {
                rejectionError.setText("Please enter a reason for rejection");
                rejectionError.setVisible(true);
                rejectionComment.setInvalid(true);
                rejectionComment.setErrorMessage("Rejection reason is required");
                return;
            }
            expenseService.rejectExpense(expense.getId(), rejectionComment.getValue());
            Notification.show("Expense rejected", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.ERROR);
            dialog.close();
            refreshGrid();
        });
        rejectButton.addThemeVariants(ButtonVariant.ERROR);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(approveButton, rejectButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        content.add(rejectionComment, rejectionError);
        dialog.add(content);
        dialog.getFooter().add(buttons);
        dialog.open();
    }

    private HorizontalLayout createDetailRow(String label, String value) {
        Span labelSpan = new Span(label + ": ");
        labelSpan.getStyle().set("font-weight", "bold").set("min-width", "100px");
        Span valueSpan = new Span(value);
        HorizontalLayout row = new HorizontalLayout(labelSpan, valueSpan);
        row.setAlignItems(FlexComponent.Alignment.BASELINE);
        return row;
    }
}
