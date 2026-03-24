package com.example.specdriven.expense;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.time.LocalDate;

@Route("submit-expense")
@PageTitle("Submit Expense — GreenLedger")
@RolesAllowed("EMPLOYEE")
public class SubmitExpenseView extends VerticalLayout {

    private final NumberField amountField;
    private final DatePicker dateField;
    private final Select<ExpenseCategory> categoryField;
    private final TextArea descriptionField;
    private final Upload receiptUpload;
    private byte[] uploadedReceiptData;
    private String uploadedReceiptFileName;
    private String uploadedReceiptContentType;

    private final ExpenseService expenseService;

    public SubmitExpenseView(ExpenseService expenseService) {
        this.expenseService = expenseService;

        setMaxWidth("600px");
        setPadding(true);
        setSpacing(true);

        H2 heading = new H2("Submit Expense");

        amountField = new NumberField("Amount");
        amountField.setMin(0.01);
        amountField.setStep(0.01);
        amountField.setPrefixComponent(new com.vaadin.flow.component.html.Span("$"));
        amountField.setWidthFull();
        amountField.setRequiredIndicatorVisible(true);

        dateField = new DatePicker("Date");
        dateField.setMax(LocalDate.now());
        dateField.setWidthFull();
        dateField.setRequiredIndicatorVisible(true);

        categoryField = new Select<>();
        categoryField.setLabel("Category");
        categoryField.setItems(ExpenseCategory.values());
        categoryField.setItemLabelGenerator(ExpenseCategory::getDisplayName);
        categoryField.setWidthFull();
        categoryField.setRequiredIndicatorVisible(true);

        descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();
        descriptionField.setMaxLength(500);
        descriptionField.setRequiredIndicatorVisible(true);

        var inMemoryHandler = UploadHandler.inMemory((metadata, data) -> {
            String contentType = metadata.contentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                Notification.show("Only JPEG and PNG images are accepted", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.ERROR);
                uploadedReceiptData = null;
                uploadedReceiptFileName = null;
                uploadedReceiptContentType = null;
                return;
            }
            uploadedReceiptData = data;
            uploadedReceiptFileName = metadata.fileName();
            uploadedReceiptContentType = contentType;
        });

        receiptUpload = new Upload(inMemoryHandler);
        receiptUpload.setAcceptedFileTypes("image/jpeg", "image/png", ".jpg", ".jpeg", ".png");
        receiptUpload.setMaxFiles(1);
        receiptUpload.setMaxFileSize(5 * 1024 * 1024); // 5MB
        receiptUpload.setWidthFull();

        receiptUpload.addFileRejectedListener(event -> {
            Notification.show(event.getErrorMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.ERROR);
        });

        receiptUpload.addFileRemovedListener(event -> {
            uploadedReceiptData = null;
            uploadedReceiptFileName = null;
            uploadedReceiptContentType = null;
        });

        Button submitButton = new Button("Submit", e -> submitExpense());
        submitButton.addThemeVariants(ButtonVariant.PRIMARY);
        submitButton.setWidthFull();

        add(heading, amountField, dateField, categoryField, descriptionField, receiptUpload, submitButton);
    }

    private void submitExpense() {
        boolean valid = true;

        if (amountField.isEmpty() || amountField.getValue() <= 0) {
            amountField.setInvalid(true);
            amountField.setErrorMessage("Amount must be a positive number");
            valid = false;
        } else {
            amountField.setInvalid(false);
        }

        if (dateField.isEmpty()) {
            dateField.setInvalid(true);
            dateField.setErrorMessage("Date is required");
            valid = false;
        } else if (dateField.getValue().isAfter(LocalDate.now())) {
            dateField.setInvalid(true);
            dateField.setErrorMessage("Date cannot be in the future");
            valid = false;
        } else {
            dateField.setInvalid(false);
        }

        if (categoryField.isEmpty()) {
            categoryField.setInvalid(true);
            categoryField.setErrorMessage("Category is required");
            valid = false;
        } else {
            categoryField.setInvalid(false);
        }

        if (descriptionField.isEmpty()) {
            descriptionField.setInvalid(true);
            descriptionField.setErrorMessage("Description is required");
            valid = false;
        } else {
            descriptionField.setInvalid(false);
        }

        if (!valid) return;

        try {
            expenseService.submitExpense(
                    BigDecimal.valueOf(amountField.getValue()),
                    dateField.getValue(),
                    categoryField.getValue(),
                    descriptionField.getValue(),
                    uploadedReceiptData,
                    uploadedReceiptFileName,
                    uploadedReceiptContentType
            );

            Notification.show("Expense submitted successfully!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.SUCCESS);

            UI.getCurrent().navigate(MyExpensesView.class);
        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.ERROR);
        }
    }
}
