package com.example.specdriven.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.specdriven.expense.SubmitExpenseView;
import com.example.specdriven.expense.MyExpensesView;
import com.example.specdriven.expense.ReviewExpensesView;
import com.example.specdriven.expense.DashboardView;

@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();

        H1 appName = new H1("GreenLedger");
        appName.getStyle()
                .set("font-size", "var(--aura-font-size-l)")
                .set("margin", "0")
                .set("color", "var(--primary)");

        Span tagline = new Span("Expenses, simplified.");
        tagline.getStyle()
                .set("font-size", "var(--aura-font-size-xs)")
                .set("color", "var(--text-secondary)");

        Button logoutButton = new Button("Log out", e -> {
            getUI().ifPresent(ui -> {
                SecurityContextHolder.clearContext();
                ui.getPage().setLocation("/login");
            });
        });
        logoutButton.addThemeVariants(ButtonVariant.TERTIARY);

        HorizontalLayout header = new HorizontalLayout(toggle, appName, tagline);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(tagline);
        header.setWidthFull();
        header.getStyle().set("padding-right", "var(--vaadin-space-m)");

        addToNavbar(header, logoutButton);

        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Submit Expense", SubmitExpenseView.class, VaadinIcon.PLUS_CIRCLE.create()));
        nav.addItem(new SideNavItem("My Expenses", MyExpensesView.class, VaadinIcon.LIST.create()));

        if (hasRole("MANAGER")) {
            nav.addItem(new SideNavItem("Review Expenses", ReviewExpensesView.class, VaadinIcon.CHECK_CIRCLE.create()));
            nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.CHART.create()));
        }

        Scroller scroller = new Scroller(nav);
        scroller.getStyle().set("padding", "var(--vaadin-space-s)");
        addToDrawer(scroller);

        setPrimarySection(Section.DRAWER);
    }

    private boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_" + role));
    }
}
