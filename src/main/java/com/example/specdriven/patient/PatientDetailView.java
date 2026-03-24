package com.example.specdriven.patient;

import com.example.specdriven.layout.MainLayout;
import com.example.specdriven.visit.Visit;
import com.example.specdriven.visit.VisitService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.util.List;

@Route(value = "patients", layout = MainLayout.class)
@PageTitle("Patient Details — Triage")
@RolesAllowed("ADMIN")
public class PatientDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final PatientService patientService;
    private final VisitService visitService;

    private Patient patient;
    private Binder<Patient> binder;
    private boolean editMode = false;

    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final DatePicker dateOfBirth = new DatePicker("Date of Birth");
    private final ComboBox<String> gender = new ComboBox<>("Gender");
    private final TextField phone = new TextField("Phone");
    private final TextField email = new TextField("Email");
    private final TextArea address = new TextArea("Address");

    private Button editButton;
    private Button saveButton;
    private Button cancelButton;

    private FormLayout formLayout;
    private Grid<Visit> visitGrid;

    public PatientDetailView(PatientService patientService, VisitService visitService) {
        this.patientService = patientService;
        this.visitService = visitService;
        setPadding(true);
        setSpacing(true);
        setSizeFull();
    }

    @Override
    public void setParameter(BeforeEvent event, Long patientId) {
        patient = patientService.findById(patientId);
        if (patient == null) {
            removeAll();
            add(new Paragraph("Patient not found"));
            return;
        }
        buildView();
    }

    private void buildView() {
        removeAll();

        H2 title = new H2(patient.getFirstName() + " " + patient.getLastName());
        title.getStyle().set("color", "#2C3E50");

        formLayout = createForm();
        setupBinder();
        setReadOnly(true);

        editButton = new Button("Edit", e -> enterEditMode());
        saveButton = new Button("Save", e -> savePatient());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setVisible(false);
        cancelButton = new Button("Cancel", e -> cancelEdit());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.setVisible(false);

        HorizontalLayout formButtons = new HorizontalLayout(editButton, saveButton, cancelButton);

        // Visit section
        H3 visitTitle = new H3("Visit History");
        visitTitle.getStyle().set("color", "#2C3E50");

        Button addVisitButton = new Button("Add Visit", e -> openVisitDialog());
        addVisitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout visitHeader = new HorizontalLayout(visitTitle, addVisitButton);
        visitHeader.setAlignItems(Alignment.BASELINE);

        visitGrid = createVisitGrid();

        add(title, formLayout, formButtons, visitHeader, visitGrid);
    }

    private FormLayout createForm() {
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("640px", 2));

        gender.setItems("Male", "Female", "Other");
        phone.setHelperText("Digits, spaces, dashes, or leading +");
        address.setMaxLength(500);

        form.add(firstName, lastName, dateOfBirth, gender, phone, email);
        form.setColspan(address, 2);
        form.add(address);

        return form;
    }

    private void setupBinder() {
        binder = new BeanValidationBinder<>(Patient.class);
        binder.forField(firstName).asRequired("First name is required").bind("firstName");
        binder.forField(lastName).asRequired("Last name is required").bind("lastName");
        binder.forField(dateOfBirth)
                .asRequired("Date of birth is required")
                .withValidator(dob -> dob.isBefore(LocalDate.now()), "Date of birth must be in the past")
                .bind("dateOfBirth");
        binder.forField(gender).bind("gender");
        binder.forField(phone)
                .withValidator(val -> val == null || val.isEmpty() || val.matches("^[+]?[0-9\\s\\-]+$"),
                        "Phone must contain only digits, spaces, dashes, or a leading +")
                .bind("phone");
        binder.forField(email)
                .withValidator(val -> val == null || val.isEmpty() || val.matches("^[^@]+@[^@]+\\.[^@]+$"),
                        "Please enter a valid email address")
                .bind("email");
        binder.forField(address).bind("address");

        binder.readBean(patient);
    }

    private void setReadOnly(boolean readOnly) {
        firstName.setReadOnly(readOnly);
        lastName.setReadOnly(readOnly);
        dateOfBirth.setReadOnly(readOnly);
        gender.setReadOnly(readOnly);
        phone.setReadOnly(readOnly);
        email.setReadOnly(readOnly);
        address.setReadOnly(readOnly);
    }

    private void enterEditMode() {
        editMode = true;
        setReadOnly(false);
        editButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    private void cancelEdit() {
        editMode = false;
        binder.readBean(patient);
        setReadOnly(true);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    private void savePatient() {
        try {
            binder.writeBean(patient);
        } catch (ValidationException e) {
            return;
        }

        patientService.save(patient);
        Notification success = Notification.show("Patient saved successfully",
                3000, Notification.Position.BOTTOM_STRETCH);
        success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        cancelEdit();
    }

    private Grid<Visit> createVisitGrid() {
        Grid<Visit> grid = new Grid<>();
        grid.addColumn(Visit::getDate).setHeader("Date").setSortable(true);
        grid.addColumn(Visit::getReason).setHeader("Reason").setAutoWidth(true);
        grid.addColumn(Visit::getDoctorName).setHeader("Doctor").setAutoWidth(true);
        grid.addColumn(Visit::getNotes).setHeader("Notes").setAutoWidth(true);
        grid.setEmptyStateText("No visits recorded yet");

        List<Visit> visits = visitService.findByPatientId(patient.getId());
        grid.setItems(visits);

        return grid;
    }

    private void refreshVisits() {
        if (visitGrid != null && patient != null) {
            List<Visit> visits = visitService.findByPatientId(patient.getId());
            visitGrid.setItems(visits);
        }
    }

    // UC-004: Visit Dialog
    private void openVisitDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Record Visit");

        DatePicker visitDate = new DatePicker("Date");
        visitDate.setValue(LocalDate.now());
        visitDate.setRequired(true);

        TextField visitReason = new TextField("Reason");
        visitReason.setRequired(true);
        visitReason.setWidthFull();

        TextField visitDoctor = new TextField("Doctor Name");
        visitDoctor.setWidthFull();

        TextArea visitNotes = new TextArea("Notes");
        visitNotes.setWidthFull();

        FormLayout visitForm = new FormLayout();
        visitForm.add(visitDate, visitReason, visitDoctor, visitNotes);
        visitForm.setColspan(visitNotes, 2);
        visitForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("400px", 2));

        dialog.add(visitForm);

        Button saveVisitButton = new Button("Save", e -> {
            if (visitDate.getValue() == null) {
                visitDate.setInvalid(true);
                visitDate.setErrorMessage("Date is required");
                return;
            }
            if (visitDate.getValue().isAfter(LocalDate.now())) {
                visitDate.setInvalid(true);
                visitDate.setErrorMessage("Visit date cannot be in the future");
                return;
            }
            if (visitReason.getValue() == null || visitReason.getValue().isBlank()) {
                visitReason.setInvalid(true);
                visitReason.setErrorMessage("Reason is required");
                return;
            }

            Visit visit = new Visit();
            visit.setDate(visitDate.getValue());
            visit.setReason(visitReason.getValue());
            visit.setDoctorName(visitDoctor.getValue());
            visit.setNotes(visitNotes.getValue());
            visit.setPatient(patient);

            visitService.save(visit);

            Notification success = Notification.show("Visit recorded successfully",
                    3000, Notification.Position.BOTTOM_STRETCH);
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            dialog.close();
            refreshVisits();
        });
        saveVisitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelVisitButton = new Button("Cancel", e -> dialog.close());
        cancelVisitButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.getFooter().add(cancelVisitButton, saveVisitButton);
        dialog.open();
    }
}
