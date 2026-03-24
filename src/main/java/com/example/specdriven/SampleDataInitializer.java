package com.example.specdriven;

import com.example.specdriven.patient.Patient;
import com.example.specdriven.patient.PatientRepository;
import com.example.specdriven.visit.Visit;
import com.example.specdriven.visit.VisitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class SampleDataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public SampleDataInitializer(PatientRepository patientRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    @Override
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            return;
        }

        Patient p1 = createPatient("Alice", "Johnson", LocalDate.of(1985, 3, 15), "Female", "+1 555-0101", "alice.johnson@email.com", "123 Oak Street");
        Patient p2 = createPatient("Bob", "Smith", LocalDate.of(1990, 7, 22), "Male", "+1 555-0102", "bob.smith@email.com", "456 Maple Avenue");
        Patient p3 = createPatient("Carol", "Williams", LocalDate.of(1978, 11, 8), "Female", "+1 555-0103", "carol.williams@email.com", "789 Pine Road");
        Patient p4 = createPatient("David", "Brown", LocalDate.of(1995, 1, 30), "Male", "+1 555-0104", "david.brown@email.com", "321 Elm Drive");
        Patient p5 = createPatient("Eva", "Davis", LocalDate.of(1988, 5, 12), "Female", "+1 555-0105", "eva.davis@email.com", "654 Cedar Lane");

        p1 = patientRepository.save(p1);
        p2 = patientRepository.save(p2);
        p3 = patientRepository.save(p3);
        p4 = patientRepository.save(p4);
        p5 = patientRepository.save(p5);

        createVisit(p1, LocalDate.now(), "Annual checkup", "Dr. Martinez", "All vitals normal");
        createVisit(p1, LocalDate.now().minusDays(90), "Follow-up", "Dr. Martinez", "Blood work results reviewed");
        createVisit(p2, LocalDate.now().minusDays(7), "Flu symptoms", "Dr. Chen", "Prescribed rest and fluids");
        createVisit(p3, LocalDate.now(), "Blood pressure check", "Dr. Patel", "Slightly elevated, monitoring");
        createVisit(p4, LocalDate.now().minusDays(30), "Vaccination", "Dr. Chen", "Flu vaccine administered");
        createVisit(p5, LocalDate.now().minusDays(2), "Sprained ankle", "Dr. Martinez", "X-ray negative, ice and rest recommended");
    }

    private Patient createPatient(String firstName, String lastName, LocalDate dob,
                                   String gender, String phone, String email, String address) {
        Patient p = new Patient();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setDateOfBirth(dob);
        p.setGender(gender);
        p.setPhone(phone);
        p.setEmail(email);
        p.setAddress(address);
        p.setCreatedAt(LocalDateTime.now());
        return p;
    }

    private void createVisit(Patient patient, LocalDate date, String reason, String doctor, String notes) {
        Visit v = new Visit();
        v.setPatient(patient);
        v.setDate(date);
        v.setReason(reason);
        v.setDoctorName(doctor);
        v.setNotes(notes);
        visitRepository.save(v);
    }
}
