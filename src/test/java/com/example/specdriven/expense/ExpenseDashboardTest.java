package com.example.specdriven.expense;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpenseDashboardTest extends SpringBrowserlessTest {

    @Autowired
    private ExpenseService expenseService;

    @BeforeEach
    @WithMockUser(username = "dashemployee", roles = "EMPLOYEE")
    void createTestData() {
        expenseService.submitExpense(new BigDecimal("50.00"), LocalDate.now().minusDays(1),
                ExpenseCategory.TRAVEL, "Taxi", null, null, null);
        expenseService.submitExpense(new BigDecimal("30.00"), LocalDate.now().minusDays(2),
                ExpenseCategory.MEALS, "Lunch", null, null, null);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void dashboardShowsSummaryCards() {
        navigate(DashboardView.class);

        List<Div> cards = $(Div.class).withClassName("dashboard-card").all();
        assertEquals(3, cards.size());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void dashboardShowsTotalPendingAmount() {
        navigate(DashboardView.class);

        Paragraph pendingLabel = $(Paragraph.class).withText("Total Pending").single();
        assertNotNull(pendingLabel);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void dashboardShowsApprovedThisMonth() {
        navigate(DashboardView.class);

        Paragraph approvedLabel = $(Paragraph.class).withText("Approved This Month").single();
        assertNotNull(approvedLabel);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void dashboardShowsPendingCount() {
        navigate(DashboardView.class);

        Paragraph countLabel = $(Paragraph.class).withText("Pending Expenses").single();
        assertNotNull(countLabel);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER", "EMPLOYEE"})
    void dashboardShowsChart() {
        navigate(DashboardView.class);

        assertTrue($(Chart.class).exists());
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void employeeCannotAccessDashboard() {
        try {
            navigate(DashboardView.class);
            fail("Employee should not be able to access dashboard");
        } catch (Exception e) {
            // Expected - access denied
        }
    }
}
