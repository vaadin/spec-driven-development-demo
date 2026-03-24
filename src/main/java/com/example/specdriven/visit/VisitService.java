package com.example.specdriven.visit;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VisitService {

    private final VisitRepository repository;

    public VisitService(VisitRepository repository) {
        this.repository = repository;
    }

    public Visit save(Visit visit) {
        return repository.save(visit);
    }

    public List<Visit> findByPatientId(Long patientId) {
        return repository.findByPatientIdOrderByDateDesc(patientId);
    }

    public long countVisitsToday() {
        return repository.countByDate(LocalDate.now());
    }

    public List<Visit> findRecentVisits() {
        return repository.findTop10ByOrderByDateDesc();
    }
}
