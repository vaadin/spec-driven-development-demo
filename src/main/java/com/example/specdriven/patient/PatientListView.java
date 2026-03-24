package com.example.specdriven.patient;

import com.example.specdriven.layout.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "patients", layout = MainLayout.class)
@PageTitle("Patients — Triage")
@RolesAllowed("ADMIN")
public class PatientListView extends VerticalLayout {

    private final PatientService patientService;
    private final Grid<Patient> grid;
    private final TextField searchField;

    public PatientListView(PatientService patientService) {
        this.patientService = patientService;

        setPadding(true);
        setSpacing(true);
        setSizeFull();

        searchField = new TextField();
        searchField.setPlaceholder("Search by name or date of birth...");
        searchField.setWidthFull();
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setClearButtonVisible(true);

        grid = createGrid();

        searchField.addValueChangeListener(e -> grid.getDataProvider().refreshAll());

        add(searchField, grid);
    }

    private Grid<Patient> createGrid() {
        Grid<Patient> grid = new Grid<>();
        grid.setSizeFull();

        grid.addColumn(Patient::getLastName)
                .setHeader("Last Name")
                .setSortProperty("lastName")
                .setAutoWidth(true);

        grid.addColumn(Patient::getFirstName)
                .setHeader("First Name")
                .setSortProperty("firstName")
                .setAutoWidth(true);

        grid.addColumn(Patient::getDateOfBirth)
                .setHeader("Date of Birth")
                .setSortProperty("dateOfBirth")
                .setAutoWidth(true);

        grid.addColumn(Patient::getPhone)
                .setHeader("Phone")
                .setSortProperty("phone")
                .setAutoWidth(true);

        grid.addColumn(Patient::getEmail)
                .setHeader("Email")
                .setSortProperty("email")
                .setAutoWidth(true);

        grid.setItems(query -> patientService.findPatients(
                query.getOffset(),
                query.getLimit(),
                query.getSortOrders(),
                searchField.getValue()
        ).stream());

        grid.setEmptyStateText("No patients found — try adjusting your search");

        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("patients/" + e.getItem().getId())));

        return grid;
    }
}
