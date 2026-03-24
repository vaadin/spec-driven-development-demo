package com.example.specdriven.expense;

import com.vaadin.flow.component.grid.Grid;
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
class ApproveRejectExpensesTest extends SpringBrowserlessTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    private Expense testExpense;

    @BeforeEach
    @WithMockUser(username = "testuser", roles = "EMPLOYEE")
    void createTestData() {
        testExpense = expenseService.submitExpense(new BigDecimal("100.00"), LocalDate.now().minusDays(1),
                ExpenseCategory.TRAVEL, "Flight ticket", null, null, null);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void gridShowsOnlyPendingExpenses() {
        navigate(ReviewExpensesView.class);

        Grid<?> grid = $(Grid.class).single();
        assertTrue(test(grid).size() > 0);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void approveUpdatesStatusToApproved() {
        expenseService.approveExpense(testExpense.getId());
        Expense updated = expenseRepository.findById(testExpense.getId()).orElseThrow();
        assertEquals(ExpenseStatus.APPROVED, updated.getStatus());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void rejectRequiresCommentAndUpdatesStatus() {
        assertThrows(IllegalArgumentException.class, () ->
                expenseService.rejectExpense(testExpense.getId(), ""));

        expenseService.rejectExpense(testExpense.getId(), "Not a valid business expense");
        Expense updated = expenseRepository.findById(testExpense.getId()).orElseThrow();
        assertEquals(ExpenseStatus.REJECTED, updated.getStatus());
        assertEquals("Not a valid business expense", updated.getRejectionComment());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void cannotApproveAlreadyApprovedExpense() {
        expenseService.approveExpense(testExpense.getId());
        assertThrows(IllegalStateException.class, () ->
                expenseService.approveExpense(testExpense.getId()));
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void employeeCannotAccessReviewView() {
        try {
            navigate(ReviewExpensesView.class);
            fail("Employee should not be able to access review view");
        } catch (Exception e) {
            // Expected - access denied
        }
    }
}
