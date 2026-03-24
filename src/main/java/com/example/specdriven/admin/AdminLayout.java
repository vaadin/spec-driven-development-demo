package com.example.specdriven.admin;

import com.example.specdriven.admin.dashboard.DashboardView;
import com.example.specdriven.admin.queue.TicketQueueView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
public class AdminLayout extends AppLayout {

    public AdminLayout() {
        DrawerToggle toggle = new DrawerToggle();

        Image logo = new Image("icons/icon.svg", "re:solve");
        logo.setWidth("24px");
        logo.setHeight("24px");

        Span title = new Span("re:solve Admin");
        title.getStyle()
                .set("font-size", "var(--aura-font-size-l)")
                .set("font-weight", "var(--aura-font-weight-semibold)");

        HorizontalLayout titleLayout = new HorizontalLayout(logo, title);
        titleLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        titleLayout.setSpacing(true);

        Anchor logout = new Anchor("/logout", "Log out");
        logout.getStyle()
                .set("font-size", "var(--aura-font-size-s)")
                .set("color", "var(--vaadin-secondary-text-color)")
                .set("margin-left", "auto")
                .set("margin-right", "var(--vaadin-gap-m)");

        HorizontalLayout navbar = new HorizontalLayout(toggle, titleLayout, logout);
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setWidthFull();
        navbar.expand(titleLayout);

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class));
        nav.addItem(new SideNavItem("Ticket Queue", TicketQueueView.class));
        nav.getStyle().set("padding", "var(--vaadin-padding-s)");

        Scroller scroller = new Scroller(nav);
        addToDrawer(scroller);
        addToNavbar(navbar);
        setPrimarySection(Section.DRAWER);
    }
}
