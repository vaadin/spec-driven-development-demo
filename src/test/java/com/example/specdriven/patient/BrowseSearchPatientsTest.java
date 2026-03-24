package com.example.specdriven.patient;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.specdriven.visit.VisitRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class BrowseSearchPatientsTest extends SpringBrowserlessTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    @BeforeEach
    void setUp() {
        visitRepository.deleteAll();
        patientRepository.deleteAll();

        createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15), "555-0101", "alice@test.com");
        createPatient("Bob", "Smith", LocalDate.of(1990, 7, 22), "555-0102", "bob@test.com");
        createPatient("Carol", "Williams", LocalDate.of(1978, 11, 8), "555-0103", "carol@test.com");
    }

    @SuppressWarnings("unchecked")
    @Test
    void tableDisplaysPatientDataWithCorrectColumns() {
        navigate(PatientListView.class);
        Grid<Patient> grid = (Grid<Patient>) $(Grid.class).single();

        assertEquals(5, grid.getColumns().size());
        assertTrue(test(grid).size() > 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void searchFiltersPatientsByName() {
        navigate(PatientListView.class);
        TextField searchField = $(TextField.class).single();
        Grid<Patient> grid = (Grid<Patient>) $(Grid.class).single();

        test(searchField).setValue("Alice");

        assertEquals(1, test(grid).size());
        assertEquals("Johnson", test(grid).getCellText(0, 0));
    }

    @SuppressWarnings("unchecked")
    @Test
    void searchFiltersPatientsByDateOfBirth() {
        navigate(PatientListView.class);
        TextField searchField = $(TextField.class).single();
        Grid<Patient> grid = (Grid<Patient>) $(Grid.class).single();

        test(searchField).setValue("1990");

        assertEquals(1, test(grid).size());
        assertEquals("Smith", test(grid).getCellText(0, 0));
    }

    @SuppressWarnings("unchecked")
    @Test
    void emptySearchShowsAllPatients() {
        navigate(PatientListView.class);
        TextField searchField = $(TextField.class).single();
        Grid<Patient> grid = (Grid<Patient>) $(Grid.class).single();

        test(searchField).setValue("Alice");
        assertEquals(1, test(grid).size());

        test(searchField).setValue("");
        assertEquals(3, test(grid).size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void listIsSortedByLastNameAscending() {
        navigate(PatientListView.class);
        Grid<Patient> grid = (Grid<Patient>) $(Grid.class).single();

        assertEquals("Johnson", test(grid).getCellText(0, 0));
        assertEquals("Smith", test(grid).getCellText(1, 0));
        assertEquals("Williams", test(grid).getCellText(2, 0));
    }

    @SuppressWarnings("unchecked")
    @Test
    void emptyStateWhenNoResultsMatch() {
        navigate(PatientListView.class);
        TextField searchField = $(TextField.class).single();
        Grid<Patient> grid = (Grid<Patient>) $(Grid.class).single();

        test(searchField).setValue("zzzzz");

        assertEquals(0, test(grid).size());
    }

    private void createPatient(String firstName, String lastName, LocalDate dob, String phone, String email) {
        Patient p = new Patient();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setDateOfBirth(dob);
        p.setPhone(phone);
        p.setEmail(email);
        p.setCreatedAt(LocalDateTime.now());
        patientRepository.save(p);
    }
}
