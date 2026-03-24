package com.example.specdriven.workload;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("team")
@PageTitle("Team Workload | Forge")
@PermitAll
public class TeamWorkloadView extends VerticalLayout {

    public TeamWorkloadView() {
        add(new H2("Team Workload"));
    }
}
