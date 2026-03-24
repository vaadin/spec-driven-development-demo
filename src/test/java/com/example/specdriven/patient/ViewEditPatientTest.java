package com.example.specdriven.patient;

import com.example.specdriven.visit.Visit;
import com.example.specdriven.visit.VisitRepository;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
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
class ViewEditPatientTest extends SpringBrowserlessTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        visitRepository.deleteAll();
        patientRepository.deleteAll();

        testPatient = new Patient();
        testPatient.setFirstName("Alice");
        testPatient.setLastName("Johnson");
        testPatient.setDateOfBirth(LocalDate.of(1985, 3, 15));
        testPatient.setPhone("555-0101");
        testPatient.setEmail("alice@test.com");
        testPatient.setCreatedAt(LocalDateTime.now());
        testPatient = patientRepository.save(testPatient);
    }

    @Test
    void patientDetailsDisplayInReadOnlyMode() {
        navigate(PatientDetailView.class, testPatient.getId());

        TextField firstNameField = $(TextField.class)
                .withPropertyValue(TextField::getLabel, "First Name").single();
        assertTrue(firstNameField.isReadOnly());
        assertEquals("Alice", firstNameField.getValue());
    }

    @Test
    void editModeEnablesFormFields() {
        navigate(PatientDetailView.class, testPatient.getId());

        Button editButton = $(Button.class).withText("Edit").single();
        test(editButton).click();

        TextField firstNameField = $(TextField.class)
                .withPropertyValue(TextField::getLabel, "First Name").single();
        assertFalse(firstNameField.isReadOnly());
    }

    @Test
    void savingValidChangesUpdatesPatient() {
        navigate(PatientDetailView.class, testPatient.getId());

        Button editButton = $(Button.class).withText("Edit").single();
        test(editButton).click();

        TextField firstNameField = $(TextField.class)
                .withPropertyValue(TextField::getLabel, "First Name").single();
        test(firstNameField).setValue("Alicia");

        Button saveButton = $(Button.class).withText("Save").single();
        test(saveButton).click();

        Patient updated = patientRepository.findById(testPatient.getId()).orElseThrow();
        assertEquals("Alicia", updated.getFirstName());
    }

    @Test
    void cancelDiscardsUnsavedChanges() {
        navigate(PatientDetailView.class, testPatient.getId());

        Button editButton = $(Button.class).withText("Edit").single();
        test(editButton).click();

        TextField firstNameField = $(TextField.class)
                .withPropertyValue(TextField::getLabel, "First Name").single();
        test(firstNameField).setValue("Changed");

        Button cancelButton = $(Button.class).withText("Cancel").single();
        test(cancelButton).click();

        assertEquals("Alice", firstNameField.getValue());
        assertTrue(firstNameField.isReadOnly());
    }

    @Test
    void visitHistoryListIsDisplayed() {
        Visit visit = new Visit();
        visit.setPatient(testPatient);
        visit.setDate(LocalDate.now());
        visit.setReason("Checkup");
        visit.setDoctorName("Dr. Smith");
        visitRepository.save(visit);

        navigate(PatientDetailView.class, testPatient.getId());

        @SuppressWarnings("unchecked")
        Grid<Visit> grid = (Grid<Visit>) $(Grid.class).single();
        assertEquals(1, test(grid).size());
    }

    @Test
    void notFoundMessageForInvalidPatientId() {
        navigate(PatientDetailView.class, 99999L);
        assertTrue($(Paragraph.class).withText("Patient not found").exists());
    }
}
