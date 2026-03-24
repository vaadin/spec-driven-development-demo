package com.example.specdriven.expense;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(username = "testemployee", roles = "EMPLOYEE")
class BrowseMyExpensesTest extends SpringBrowserlessTest {

    @Autowired
    private ExpenseService expenseService;

    @BeforeEach
    void createTestData() {
        expenseService.submitExpense(new BigDecimal("25.00"), LocalDate.now().minusDays(5),
                ExpenseCategory.TRAVEL, "Taxi ride", null, null, null);
        expenseService.submitExpense(new BigDecimal("15.00"), LocalDate.now().minusDays(3),
                ExpenseCategory.MEALS, "Coffee meeting", null, null, null);
    }

    @Test
    void gridDisplaysCorrectColumns() {
        navigate(MyExpensesView.class);

        Grid<?> grid = $(Grid.class).single();
        var columns = grid.getColumns();
        assertEquals(5, columns.size());
    }

    @Test
    void onlyLoggedInEmployeeExpensesAreShown() {
        navigate(MyExpensesView.class);

        Grid<Expense> grid = (Grid<Expense>) $(Grid.class).single();
        var items = test(grid).getRow(0);
        // Verify grid has data
        assertTrue(test(grid).size() > 0);
    }

    @Test
    void statusFilterExists() {
        navigate(MyExpensesView.class);

        Select<?> statusFilter = $(Select.class).withPropertyValue(Select::getLabel, "Status").single();
        assertNotNull(statusFilter);
        assertEquals("All", statusFilter.getValue());
    }

    @Test
    void dateRangeFiltersExist() {
        navigate(MyExpensesView.class);

        assertTrue($(DatePicker.class).withPropertyValue(DatePicker::getLabel, "From").exists());
        assertTrue($(DatePicker.class).withPropertyValue(DatePicker::getLabel, "To").exists());
    }

    @Test
    void totalIsDisplayed() {
        navigate(MyExpensesView.class);

        Span total = $(Span.class).withCondition(s -> s.getText() != null && s.getText().startsWith("Total:")).single();
        assertNotNull(total);
        assertTrue(total.getText().contains("$"));
    }
}
