package com.example.specdriven.show;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ShowService {

    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    public List<Show> findAllSorted() {
        return showRepository.findAllByOrderByDateTimeAsc();
    }

    public Optional<Show> findById(Long id) {
        return showRepository.findById(id);
    }
}
