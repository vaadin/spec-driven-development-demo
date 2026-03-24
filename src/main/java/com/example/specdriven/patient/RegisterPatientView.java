package com.example.specdriven.patient;

import com.example.specdriven.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.util.List;

@Route(value = "patients/new", layout = MainLayout.class)
@PageTitle("Register Patient — Triage")
@RolesAllowed("ADMIN")
public class RegisterPatientView extends VerticalLayout {

    private final PatientService patientService;
    private final Binder<Patient> binder;

    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final DatePicker dateOfBirth = new DatePicker("Date of Birth");
    private final ComboBox<String> gender = new ComboBox<>("Gender");
    private final TextField phone = new TextField("Phone");
    private final TextField email = new TextField("Email");
    private final TextArea address = new TextArea("Address");

    public RegisterPatientView(PatientService patientService) {
        this.patientService = patientService;

        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Register New Patient");
        title.getStyle().set("color", "#2C3E50");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("640px", 2));

        firstName.setRequired(true);
        lastName.setRequired(true);
        dateOfBirth.setRequired(true);
        gender.setItems("Male", "Female", "Other");
        phone.setHelperText("Digits, spaces, dashes, or leading +");
        email.setHelperText("Optional");
        address.setMaxLength(500);

        formLayout.add(firstName, lastName, dateOfBirth, gender, phone, email);
        formLayout.setColspan(address, 2);
        formLayout.add(address);

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

        Button saveButton = new Button("Save", e -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e ->
                getUI().ifPresent(ui -> ui.navigate("patients")));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);

        add(title, formLayout, buttons);
    }

    private void save() {
        Patient patient = new Patient();
        try {
            binder.writeBean(patient);
        } catch (ValidationException e) {
            return;
        }

        // Duplicate detection
        List<Patient> duplicates = patientService.findDuplicates(
                patient.getFirstName(), patient.getLastName(), patient.getDateOfBirth());
        if (!duplicates.isEmpty()) {
            Notification warning = Notification.show(
                    "Warning: A patient with the same name and date of birth already exists",
                    5000, Notification.Position.BOTTOM_STRETCH);
            warning.addThemeVariants(NotificationVariant.LUMO_WARNING);
        }

        Patient saved = patientService.save(patient);
        Notification success = Notification.show("Patient saved successfully",
                3000, Notification.Position.BOTTOM_STRETCH);
        success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        getUI().ifPresent(ui -> ui.navigate("patients/" + saved.getId()));
    }
}
