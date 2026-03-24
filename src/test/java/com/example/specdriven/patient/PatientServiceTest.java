package com.example.specdriven.patient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.specdriven.visit.VisitRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PatientServiceTest {

    @Autowired
    private PatientService patientService;

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
    void findPatientsReturnsAllWhenNoFilter() {
        createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15));
        createPatient("Bob", "Smith", LocalDate.of(1990, 7, 22));

        List<Patient> results = patientService.findPatients(0, 10, Collections.emptyList(), "");
        assertEquals(2, results.size());
    }

    @Test
    void findPatientsFiltersByFirstName() {
        createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15));
        createPatient("Bob", "Smith", LocalDate.of(1990, 7, 22));

        List<Patient> results = patientService.findPatients(0, 10, Collections.emptyList(), "Alice");
        assertEquals(1, results.size());
        assertEquals("Alice", results.get(0).getFirstName());
    }

    @Test
    void findPatientsFiltersByLastName() {
        createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15));
        createPatient("Bob", "Smith", LocalDate.of(1990, 7, 22));

        List<Patient> results = patientService.findPatients(0, 10, Collections.emptyList(), "Smith");
        assertEquals(1, results.size());
        assertEquals("Bob", results.get(0).getFirstName());
    }

    @Test
    void findPatientsFiltersByDateOfBirth() {
        createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15));
        createPatient("Bob", "Smith", LocalDate.of(1990, 7, 22));

        List<Patient> results = patientService.findPatients(0, 10, Collections.emptyList(), "1985");
        assertEquals(1, results.size());
        assertEquals("Alice", results.get(0).getFirstName());
    }

    @Test
    void findPatientsSortsByLastNameByDefault() {
        createPatient("Bob", "Smith", LocalDate.of(1990, 7, 22));
        createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15));
        createPatient("Carol", "Williams", LocalDate.of(1978, 11, 8));

        List<Patient> results = patientService.findPatients(0, 10, Collections.emptyList(), "");
        assertEquals("Johnson", results.get(0).getLastName());
        assertEquals("Smith", results.get(1).getLastName());
        assertEquals("Williams", results.get(2).getLastName());
    }

    @Test
    void findDuplicatesDetectsMatchingPatients() {
        createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15));

        List<Patient> duplicates = patientService.findDuplicates("Alice", "Johnson", LocalDate.of(1985, 3, 15));
        assertEquals(1, duplicates.size());
    }

    @Test
    void findDuplicatesReturnsEmptyWhenNoMatch() {
        createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15));

        List<Patient> duplicates = patientService.findDuplicates("Bob", "Smith", LocalDate.of(1990, 7, 22));
        assertTrue(duplicates.isEmpty());
    }

    @Test
    void saveAndFindById() {
        Patient p = createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15));
        Patient found = patientService.findById(p.getId());

        assertNotNull(found);
        assertEquals("Alice", found.getFirstName());
    }

    @Test
    void findByIdReturnsNullForNonExistent() {
        assertNull(patientService.findById(999L));
    }

    private Patient createPatient(String firstName, String lastName, LocalDate dob) {
        Patient p = new Patient();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setDateOfBirth(dob);
        p.setCreatedAt(LocalDateTime.now());
        return patientRepository.save(p);
    }
}
