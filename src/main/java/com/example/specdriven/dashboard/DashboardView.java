package com.example.specdriven.dashboard;

import com.example.specdriven.layout.MainLayout;
import com.example.specdriven.patient.Patient;
import com.example.specdriven.patient.PatientService;
import com.example.specdriven.visit.Visit;
import com.example.specdriven.visit.VisitService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard — Triage")
@RolesAllowed("ADMIN")
public class DashboardView extends VerticalLayout {

    private final PatientService patientService;
    private final VisitService visitService;

    public DashboardView(PatientService patientService, VisitService visitService) {
        this.patientService = patientService;
        this.visitService = visitService;

        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Dashboard");
        title.getStyle().set("color", "#2C3E50");

        FlexLayout statsRow = createStatsRow();
        HorizontalLayout listsRow = createListsRow();

        add(title, statsRow, listsRow);
    }

    private FlexLayout createStatsRow() {
        long totalPatients = patientService.count();
        long visitsToday = visitService.countVisitsToday();
        long patientsThisMonth = patientService.countRegisteredThisMonth();

        Div totalCard = createStatCard(String.valueOf(totalPatients), "Total Patients");
        Div visitsCard = createStatCard(String.valueOf(visitsToday), "Visits Today");
        Div newPatientsCard = createStatCard(String.valueOf(patientsThisMonth), "New This Month");

        FlexLayout row = new FlexLayout(totalCard, visitsCard, newPatientsCard);
        row.setWidthFull();
        row.getStyle()
                .set("gap", "var(--vaadin-gap-m)")
                .set("flex-wrap", "wrap");
        totalCard.getStyle().set("flex", "1 1 200px");
        visitsCard.getStyle().set("flex", "1 1 200px");
        newPatientsCard.getStyle().set("flex", "1 1 200px");

        return row;
    }

    private Div createStatCard(String value, String label) {
        Div card = new Div();
        card.addClassName("stat-card");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("stat-value");
        valueSpan.getStyle().set("display", "block");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("stat-label");
        labelSpan.getStyle().set("display", "block");

        card.add(valueSpan, labelSpan);
        return card;
    }

    private HorizontalLayout createListsRow() {
        VerticalLayout recentVisitsSection = createRecentVisitsSection();
        VerticalLayout recentPatientsSection = createRecentPatientsSection();

        HorizontalLayout row = new HorizontalLayout(recentVisitsSection, recentPatientsSection);
        row.setWidthFull();
        row.getStyle().set("gap", "var(--vaadin-gap-m)");
        recentVisitsSection.getStyle().set("flex", "1 1 0");
        recentPatientsSection.getStyle().set("flex", "1 1 0");

        return row;
    }

    private VerticalLayout createRecentVisitsSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("card");
        section.setPadding(true);
        section.setSpacing(true);

        H3 heading = new H3("Recent Visits");
        heading.getStyle().set("color", "#2C3E50").set("margin", "0");
        section.add(heading);

        List<Visit> recentVisits = visitService.findRecentVisits();
        if (recentVisits.isEmpty()) {
            Span empty = new Span("No visits yet");
            empty.getStyle().set("color", "#6B7D8D");
            section.add(empty);
        } else {
            for (Visit visit : recentVisits) {
                Div row = new Div();
                row.getStyle()
                        .set("display", "flex")
                        .set("justify-content", "space-between")
                        .set("align-items", "center")
                        .set("padding", "var(--vaadin-padding-s) 0")
                        .set("border-bottom", "1px solid #D6E0E8");

                Patient patient = visit.getPatient();
                RouterLink patientLink = new RouterLink(
                        patient.getFirstName() + " " + patient.getLastName(),
                        com.example.specdriven.patient.PatientDetailView.class,
                        patient.getId());
                patientLink.getStyle().set("color", "var(--vaadin-primary-color-text)");

                Span details = new Span(visit.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                        + " — " + visit.getReason());
                details.getStyle().set("color", "#6B7D8D").set("font-size", "var(--aura-font-size-s)");

                Div left = new Div(patientLink, details);
                left.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "2px");

                row.add(left);
                section.add(row);
            }
        }

        return section;
    }

    private VerticalLayout createRecentPatientsSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("card");
        section.setPadding(true);
        section.setSpacing(true);

        H3 heading = new H3("Recently Registered");
        heading.getStyle().set("color", "#2C3E50").set("margin", "0");
        section.add(heading);

        List<Patient> recentPatients = patientService.findRecentlyRegistered();
        if (recentPatients.isEmpty()) {
            Span empty = new Span("No patients yet");
            empty.getStyle().set("color", "#6B7D8D");
            section.add(empty);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Patient patient : recentPatients) {
                Div row = new Div();
                row.getStyle()
                        .set("display", "flex")
                        .set("justify-content", "space-between")
                        .set("align-items", "center")
                        .set("padding", "var(--vaadin-padding-s) 0")
                        .set("border-bottom", "1px solid #D6E0E8");

                RouterLink patientLink = new RouterLink(
                        patient.getFirstName() + " " + patient.getLastName(),
                        com.example.specdriven.patient.PatientDetailView.class,
                        patient.getId());
                patientLink.getStyle().set("color", "var(--vaadin-primary-color-text)");

                Span date = new Span("Registered " + patient.getCreatedAt().format(formatter));
                date.getStyle().set("color", "#6B7D8D").set("font-size", "var(--aura-font-size-s)");

                Div left = new Div(patientLink, date);
                left.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "2px");

                row.add(left);
                section.add(row);
            }
        }

        return section;
    }
}
