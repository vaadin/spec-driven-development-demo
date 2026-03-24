package com.example.specdriven.admin;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("admin")
@PageTitle("Admin — CineMax")
@RolesAllowed("ADMIN")
public class AdminIndexView extends VerticalLayout {

    public AdminIndexView() {
        setPadding(true);
        setSpacing(true);

        add(new H1("CineMax Admin"));
        add(new Anchor("admin/movies", "Manage Movies"));
        add(new Anchor("admin/shows", "Manage Shows"));
        add(new Anchor("/", "← Back to Public Site"));
    }
}
