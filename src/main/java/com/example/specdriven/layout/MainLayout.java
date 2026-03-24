package com.example.specdriven.layout;

import com.example.specdriven.project.ProjectDashboardView;
import com.example.specdriven.workload.TeamWorkloadView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import jakarta.annotation.security.PermitAll;

@Layout
@PermitAll
public class MainLayout extends AppLayout {

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Forge");
        title.getStyle()
                .set("font-size", "var(--aura-font-size-l)")
                .set("margin", "0")
                .set("color", "var(--vaadin-primary-color)");

        HorizontalLayout header = new HorizontalLayout(toggle, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.getStyle().set("padding", "0 var(--vaadin-space-m)");

        addToNavbar(header);

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Projects", ProjectDashboardView.class,
                VaadinIcon.TASKS.create()));
        nav.addItem(new SideNavItem("Team Workload", TeamWorkloadView.class,
                VaadinIcon.USERS.create()));

        Scroller scroller = new Scroller(nav);
        scroller.getStyle().set("padding", "var(--vaadin-space-s)");
        addToDrawer(scroller);

        setPrimarySection(Section.DRAWER);
    }
}
