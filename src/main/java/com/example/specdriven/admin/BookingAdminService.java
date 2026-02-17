package com.example.specdriven.admin;

import com.example.specdriven.data.Booking;
import com.example.specdriven.data.BookingRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookingAdminService {

    private final BookingRepository bookingRepository;

    public BookingAdminService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAllWithDetails();
    }

    public List<Booking> search(String term) {
        if (term == null || term.isBlank()) {
            return findAll();
        }
        return bookingRepository.searchByTerm(term.strip());
    }
}
