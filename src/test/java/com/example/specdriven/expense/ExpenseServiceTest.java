package com.example.specdriven.expense;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpenseServiceTest {

    @Autowired
    private ExpenseService expenseService;

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void submitExpenseCreatesWithPendingStatus() {
        Expense expense = expenseService.submitExpense(
                new BigDecimal("25.00"),
                LocalDate.now().minusDays(1),
                ExpenseCategory.TRAVEL,
                "Taxi ride",
                null, null, null
        );

        assertNotNull(expense.getId());
        assertEquals(ExpenseStatus.PENDING, expense.getStatus());
        assertEquals("employee", expense.getSubmittedBy());
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void submitExpenseRejectsNullAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                expenseService.submitExpense(null, LocalDate.now(), ExpenseCategory.TRAVEL, "Test", null, null, null));
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void submitExpenseRejectsZeroAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                expenseService.submitExpense(BigDecimal.ZERO, LocalDate.now(), ExpenseCategory.TRAVEL, "Test", null, null, null));
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void submitExpenseRejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                expenseService.submitExpense(new BigDecimal("-10"), LocalDate.now(), ExpenseCategory.TRAVEL, "Test", null, null, null));
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void submitExpenseRejectsFutureDate() {
        assertThrows(IllegalArgumentException.class, () ->
                expenseService.submitExpense(new BigDecimal("10"), LocalDate.now().plusDays(1), ExpenseCategory.TRAVEL, "Test", null, null, null));
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void submitExpenseRejectsNullCategory() {
        assertThrows(IllegalArgumentException.class, () ->
                expenseService.submitExpense(new BigDecimal("10"), LocalDate.now(), null, "Test", null, null, null));
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void submitExpenseRejectsBlankDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                expenseService.submitExpense(new BigDecimal("10"), LocalDate.now(), ExpenseCategory.TRAVEL, "", null, null, null));
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void getMyExpensesReturnsOnlyCurrentUserExpenses() {
        expenseService.submitExpense(new BigDecimal("10"), LocalDate.now(), ExpenseCategory.MEALS, "My expense", null, null, null);
        List<Expense> myExpenses = expenseService.getMyExpenses();
        assertTrue(myExpenses.stream().allMatch(e -> e.getSubmittedBy().equals("employee")));
    }
}
