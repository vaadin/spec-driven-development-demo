package com.example.specdriven.visit;

import com.example.specdriven.patient.Patient;
import com.example.specdriven.patient.PatientDetailView;
import com.example.specdriven.patient.PatientRepository;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
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
class RecordVisitTest extends SpringBrowserlessTest {

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
        testPatient.setCreatedAt(LocalDateTime.now());
        testPatient = patientRepository.save(testPatient);
    }

    @Test
    void addVisitOpensDialog() {
        navigate(PatientDetailView.class, testPatient.getId());

        Button addVisitButton = $(Button.class).withText("Add Visit").single();
        test(addVisitButton).click();

        assertTrue($(Dialog.class).exists());
    }

    @Test
    void dateDefaultsToToday() {
        navigate(PatientDetailView.class, testPatient.getId());

        Button addVisitButton = $(Button.class).withText("Add Visit").single();
        test(addVisitButton).click();

        Dialog dialog = $(Dialog.class).single();
        DatePicker datePicker = $(DatePicker.class, dialog).single();
        assertEquals(LocalDate.now(), datePicker.getValue());
    }

    @Test
    void successfulSaveAddsVisitToHistory() {
        navigate(PatientDetailView.class, testPatient.getId());

        Button addVisitButton = $(Button.class).withText("Add Visit").single();
        test(addVisitButton).click();

        Dialog dialog = $(Dialog.class).single();
        TextField reasonField = $(TextField.class, dialog)
                .withPropertyValue(TextField::getLabel, "Reason").single();
        test(reasonField).setValue("Annual checkup");

        Button saveButton = $(Button.class, dialog).withText("Save").single();
        test(saveButton).click();

        assertEquals(1, visitRepository.count());
        Visit saved = visitRepository.findAll().get(0);
        assertEquals("Annual checkup", saved.getReason());
        assertEquals(LocalDate.now(), saved.getDate());
    }

    @Test
    void visitListSortedByDateDescending() {
        createVisit(LocalDate.now().minusDays(30), "Visit 1");
        createVisit(LocalDate.now(), "Visit 2");
        createVisit(LocalDate.now().minusDays(10), "Visit 3");

        navigate(PatientDetailView.class, testPatient.getId());

        @SuppressWarnings("unchecked")
        Grid<Visit> grid = (Grid<Visit>) $(Grid.class).single();
        assertEquals(3, test(grid).size());
        // Most recent first
        assertEquals("Visit 2", test(grid).getCellText(0, 1));
    }

    private void createVisit(LocalDate date, String reason) {
        Visit v = new Visit();
        v.setPatient(testPatient);
        v.setDate(date);
        v.setReason(reason);
        v.setDoctorName("Dr. Test");
        visitRepository.save(v);
    }
}
