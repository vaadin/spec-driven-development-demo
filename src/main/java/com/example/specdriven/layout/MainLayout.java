package com.example.specdriven.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
public class MainLayout extends AppLayout {

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();

        Span triPart = new Span("Tri");
        triPart.getStyle().set("color", "#2C3E50");
        Span agePart = new Span("age");
        agePart.getStyle().set("color", "#4A90B8");
        H1 title = new H1(triPart, agePart);
        title.getStyle()
                .set("font-size", "var(--aura-font-size-l)")
                .set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(toggle, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding", "0 var(--vaadin-padding-m)");

        SideNav nav = createSideNav();
        nav.getStyle().set("padding", "var(--vaadin-padding-s)");

        Scroller scroller = new Scroller(nav);

        addToDrawer(scroller);
        addToNavbar(header);

        setPrimarySection(Section.DRAWER);
    }

    private SideNav createSideNav() {
        SideNav nav = new SideNav();

        SideNavItem dashboardItem = new SideNavItem("Dashboard",
                "dashboard", VaadinIcon.DASHBOARD.create());

        SideNavItem patientsItem = new SideNavItem("Patients",
                "patients", VaadinIcon.GROUP.create());
        patientsItem.setMatchNested(true);

        nav.addItem(dashboardItem, patientsItem);
        return nav;
    }
}
