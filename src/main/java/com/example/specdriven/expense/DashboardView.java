package com.example.specdriven.expense;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.util.Map;

@Route("dashboard")
@PageTitle("Dashboard — GreenLedger")
@RolesAllowed("MANAGER")
public class DashboardView extends VerticalLayout {

    public DashboardView(ExpenseService expenseService) {
        setPadding(true);
        setSpacing(true);

        H2 heading = new H2("Dashboard");

        // Summary cards
        BigDecimal totalPending = expenseService.getTotalPendingAmount();
        BigDecimal approvedThisMonth = expenseService.getApprovedAmountThisMonth();
        long pendingCount = expenseService.getPendingCount();

        Div pendingAmountCard = createCard("Total Pending", String.format("$%.2f", totalPending));
        Div approvedMonthCard = createCard("Approved This Month", String.format("$%.2f", approvedThisMonth));
        Div pendingCountCard = createCard("Pending Expenses", String.valueOf(pendingCount));

        HorizontalLayout cards = new HorizontalLayout(pendingAmountCard, approvedMonthCard, pendingCountCard);
        cards.setWidthFull();
        cards.getStyle().set("flex-wrap", "wrap");

        // Category breakdown chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Approved Expenses by Category");

        DataSeries series = new DataSeries();
        Map<ExpenseCategory, BigDecimal> categoryTotals = expenseService.getApprovedByCategory();
        for (Map.Entry<ExpenseCategory, BigDecimal> entry : categoryTotals.entrySet()) {
            series.add(new DataSeriesItem(entry.getKey().getDisplayName(), entry.getValue().doubleValue()));
        }

        if (series.getData().isEmpty()) {
            series.add(new DataSeriesItem("No data", 0));
        }

        conf.addSeries(series);
        chart.setWidthFull();
        chart.setHeight("400px");

        add(heading, cards, chart);
    }

    private Div createCard(String label, String value) {
        Div card = new Div();
        card.addClassName("dashboard-card");

        Paragraph labelP = new Paragraph(label);
        labelP.addClassName("card-label");

        Paragraph valueP = new Paragraph(value);
        valueP.addClassName("card-value");

        card.add(labelP, valueP);
        return card;
    }
}
