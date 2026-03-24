package com.example.specdriven.patient;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public List<Patient> findPatients(int offset, int limit,
            List<QuerySortOrder> sortOrders, String filter) {
        Sort sort = toSort(sortOrders);
        Pageable pageable = PageRequest.of(offset / Math.max(limit, 1), Math.max(limit, 1), sort);
        if (filter == null || filter.isBlank()) {
            return repository.findAll(pageable).getContent();
        }
        return repository.findByFilter(filter.trim(), pageable).getContent();
    }

    public Patient save(Patient patient) {
        if (patient.getCreatedAt() == null) {
            patient.setCreatedAt(LocalDateTime.now());
        }
        return repository.save(patient);
    }

    public Patient findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Patient> findDuplicates(String firstName, String lastName, LocalDate dateOfBirth) {
        return repository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndDateOfBirth(
                firstName, lastName, dateOfBirth);
    }

    public long count() {
        return repository.count();
    }

    public long countRegisteredThisMonth() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return repository.countByCreatedAtAfter(startOfMonth);
    }

    public List<Patient> findRecentlyRegistered() {
        return repository.findTop5ByOrderByCreatedAtDesc();
    }

    private Sort toSort(List<QuerySortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) {
            return Sort.by(Sort.Direction.ASC, "lastName");
        }
        List<Sort.Order> orders = sortOrders.stream()
                .map(so -> new Sort.Order(
                        so.getDirection() == SortDirection.ASCENDING
                                ? Sort.Direction.ASC : Sort.Direction.DESC,
                        mapSortProperty(so.getSorted())))
                .toList();
        return Sort.by(orders);
    }

    private String mapSortProperty(String property) {
        return switch (property) {
            case "lastName" -> "lastName";
            case "firstName" -> "firstName";
            case "dateOfBirth" -> "dateOfBirth";
            case "phone" -> "phone";
            case "email" -> "email";
            default -> "lastName";
        };
    }
}
