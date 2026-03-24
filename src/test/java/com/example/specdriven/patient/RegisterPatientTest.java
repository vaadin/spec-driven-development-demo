package com.example.specdriven.patient;

import com.example.specdriven.visit.VisitRepository;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class RegisterPatientTest extends SpringBrowserlessTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    @BeforeEach
    void setUp() {
        visitRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    void formRendersAllFields() {
        navigate(RegisterPatientView.class);

        assertTrue($(TextField.class).withPropertyValue(TextField::getLabel, "First Name").exists());
        assertTrue($(TextField.class).withPropertyValue(TextField::getLabel, "Last Name").exists());
        assertTrue($(DatePicker.class).withPropertyValue(DatePicker::getLabel, "Date of Birth").exists());
        assertTrue($(ComboBox.class).exists());
        assertTrue($(TextField.class).withPropertyValue(TextField::getLabel, "Phone").exists());
        assertTrue($(TextField.class).withPropertyValue(TextField::getLabel, "Email").exists());
        assertTrue($(TextArea.class).withPropertyValue(TextArea::getLabel, "Address").exists());
    }

    @Test
    void requiredFieldValidation() {
        navigate(RegisterPatientView.class);

        // Click save without filling anything
        Button saveButton = $(Button.class).withText("Save").single();
        test(saveButton).click();

        // Should still be on registration page (not navigated)
        // Verify the patient was not saved
        assertEquals(0, patientRepository.count());
    }

    @Test
    void dateOfBirthMustBeInThePast() {
        navigate(RegisterPatientView.class);

        TextField firstNameField = $(TextField.class).withPropertyValue(TextField::getLabel, "First Name").single();
        TextField lastNameField = $(TextField.class).withPropertyValue(TextField::getLabel, "Last Name").single();
        DatePicker dobField = $(DatePicker.class).single();

        test(firstNameField).setValue("Test");
        test(lastNameField).setValue("Patient");
        test(dobField).setValue(LocalDate.now().plusDays(1));

        Button saveButton = $(Button.class).withText("Save").single();
        test(saveButton).click();

        assertEquals(0, patientRepository.count());
    }

    @Test
    void emailFormatValidation() {
        navigate(RegisterPatientView.class);

        TextField firstNameField = $(TextField.class).withPropertyValue(TextField::getLabel, "First Name").single();
        TextField lastNameField = $(TextField.class).withPropertyValue(TextField::getLabel, "Last Name").single();
        DatePicker dobField = $(DatePicker.class).single();
        TextField emailField = $(TextField.class).withPropertyValue(TextField::getLabel, "Email").single();

        test(firstNameField).setValue("Test");
        test(lastNameField).setValue("Patient");
        test(dobField).setValue(LocalDate.of(1990, 1, 1));
        test(emailField).setValue("not-an-email");

        Button saveButton = $(Button.class).withText("Save").single();
        test(saveButton).click();

        assertEquals(0, patientRepository.count());
    }

    @Test
    void successfulSaveCreatesPatient() {
        navigate(RegisterPatientView.class);

        TextField firstNameField = $(TextField.class).withPropertyValue(TextField::getLabel, "First Name").single();
        TextField lastNameField = $(TextField.class).withPropertyValue(TextField::getLabel, "Last Name").single();
        DatePicker dobField = $(DatePicker.class).single();

        test(firstNameField).setValue("Jane");
        test(lastNameField).setValue("Doe");
        test(dobField).setValue(LocalDate.of(1990, 6, 15));

        Button saveButton = $(Button.class).withText("Save").single();
        test(saveButton).click();

        assertEquals(1, patientRepository.count());
        Patient saved = patientRepository.findAll().get(0);
        assertEquals("Jane", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());

        // Notification should be shown
        assertTrue($(Notification.class).exists());
    }

    @Test
    void duplicateDetectionWarning() {
        // Create an existing patient
        Patient existing = new Patient();
        existing.setFirstName("Jane");
        existing.setLastName("Doe");
        existing.setDateOfBirth(LocalDate.of(1990, 6, 15));
        existing.setCreatedAt(LocalDateTime.now());
        patientRepository.save(existing);

        navigate(RegisterPatientView.class);

        TextField firstNameField = $(TextField.class).withPropertyValue(TextField::getLabel, "First Name").single();
        TextField lastNameField = $(TextField.class).withPropertyValue(TextField::getLabel, "Last Name").single();
        DatePicker dobField = $(DatePicker.class).single();

        test(firstNameField).setValue("Jane");
        test(lastNameField).setValue("Doe");
        test(dobField).setValue(LocalDate.of(1990, 6, 15));

        Button saveButton = $(Button.class).withText("Save").single();
        test(saveButton).click();

        // Should still save (warning, not blocking)
        assertEquals(2, patientRepository.count());
    }
}
