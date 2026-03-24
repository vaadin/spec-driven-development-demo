package com.example.specdriven;

import com.example.specdriven.dashboard.DashboardView;
import com.example.specdriven.inventory.InventoryView;
import com.example.specdriven.product.ProductsView;
import com.example.specdriven.stock.AdjustStockView;
import com.example.specdriven.stock.ReceiveStockView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;

@Layout
@PermitAll
public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);

        DrawerToggle toggle = new DrawerToggle();

        H1 viewTitle = new H1();
        viewTitle.getStyle().set("font-size", "var(--aura-font-size-l)")
                .set("margin", "0");

        addToNavbar(toggle, viewTitle);

        Image logo = new Image("images/logo.svg", "Stash.log");
        logo.setWidth("160px");
        logo.getStyle().set("margin", "var(--vaadin-padding-m)");

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class,
                VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Inventory", InventoryView.class,
                VaadinIcon.STOCK.create()));
        nav.addItem(new SideNavItem("Receive Stock", ReceiveStockView.class,
                VaadinIcon.PACKAGE.create()));
        nav.addItem(new SideNavItem("Products", ProductsView.class,
                VaadinIcon.CUBE.create()));
        nav.addItem(new SideNavItem("Adjust Stock", AdjustStockView.class,
                VaadinIcon.ADJUST.create()));
        nav.getStyle().set("margin", "var(--vaadin-gap-s)");

        Scroller scroller = new Scroller(nav);
        scroller.getStyle().set("flex", "1");

        Footer footer = new Footer();
        Anchor logout = new Anchor("/logout", "Log out");
        logout.getStyle().set("padding", "var(--vaadin-padding-m)")
                .set("display", "block")
                .set("color", "var(--vaadin-text-color-secondary)");
        footer.add(logout);

        VerticalLayout drawerContent = new VerticalLayout(logo, scroller, footer);
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);

        addToDrawer(drawerContent);
    }
}
