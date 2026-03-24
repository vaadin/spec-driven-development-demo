package com.example.specdriven.expense;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(username = "employee", roles = "EMPLOYEE")
class SubmitExpenseTest extends SpringBrowserlessTest {

    @Test
    void formDisplaysAllRequiredFields() {
        navigate(SubmitExpenseView.class);

        assertTrue($(NumberField.class).withPropertyValue(NumberField::getLabel, "Amount").exists());
        assertTrue($(DatePicker.class).withPropertyValue(DatePicker::getLabel, "Date").exists());
        assertTrue($(Select.class).withPropertyValue(Select::getLabel, "Category").exists());
        assertTrue($(TextArea.class).withPropertyValue(TextArea::getLabel, "Description").exists());
        assertTrue($(Upload.class).exists());
        assertTrue($(Button.class).withText("Submit").exists());
    }

    @Test
    void validationErrorsShownForMissingMandatoryFields() {
        navigate(SubmitExpenseView.class);

        Button submit = $(Button.class).withText("Submit").single();
        test(submit).click();

        NumberField amount = $(NumberField.class).withPropertyValue(NumberField::getLabel, "Amount").single();
        DatePicker date = $(DatePicker.class).withPropertyValue(DatePicker::getLabel, "Date").single();
        Select<?> category = $(Select.class).withPropertyValue(Select::getLabel, "Category").single();
        TextArea description = $(TextArea.class).withPropertyValue(TextArea::getLabel, "Description").single();

        assertTrue(amount.isInvalid());
        assertTrue(date.isInvalid());
        assertTrue(category.isInvalid());
        assertTrue(description.isInvalid());
    }

    @Test
    void amountFieldHasMinimumConstraint() {
        navigate(SubmitExpenseView.class);

        NumberField amount = $(NumberField.class).withPropertyValue(NumberField::getLabel, "Amount").single();
        assertEquals(0.01, amount.getMin());
    }

    @Test
    void dateRejectsFutureDates() {
        navigate(SubmitExpenseView.class);

        NumberField amount = $(NumberField.class).withPropertyValue(NumberField::getLabel, "Amount").single();
        DatePicker date = $(DatePicker.class).withPropertyValue(DatePicker::getLabel, "Date").single();

        assertEquals(LocalDate.now(), date.getMax());
    }

    @Test
    void successfulSubmissionCreatesExpenseWithPendingStatus() {
        navigate(SubmitExpenseView.class);

        NumberField amount = $(NumberField.class).withPropertyValue(NumberField::getLabel, "Amount").single();
        test(amount).setValue(42.50);

        DatePicker date = $(DatePicker.class).withPropertyValue(DatePicker::getLabel, "Date").single();
        test(date).setValue(LocalDate.now().minusDays(1));

        Select<ExpenseCategory> category = (Select<ExpenseCategory>) $(Select.class)
                .withPropertyValue(Select::getLabel, "Category").single();
        test(category).selectItem("Meals");

        TextArea description = $(TextArea.class).withPropertyValue(TextArea::getLabel, "Description").single();
        test(description).setValue("Team lunch");

        Button submit = $(Button.class).withText("Submit").single();
        test(submit).click();

        Notification notification = $(Notification.class).single();
        assertEquals("Expense submitted successfully!", test(notification).getText());
    }
}
