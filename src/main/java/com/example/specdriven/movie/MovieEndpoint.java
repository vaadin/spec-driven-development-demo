package com.example.specdriven.movie;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@BrowserCallable
@Service
@AnonymousAllowed
public class MovieEndpoint {

    private final MovieRepository movieRepository;

    public MovieEndpoint(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public record MovieSummary(Long id, String title, int durationMinutes, String posterFileName) {
    }

    public List<MovieSummary> getMoviesWithFutureShows() {
        return movieRepository.findMoviesWithFutureShows(LocalDateTime.now())
                .stream()
                .map(m -> new MovieSummary(m.getId(), m.getTitle(), m.getDurationMinutes(), m.getPosterFileName()))
                .toList();
    }
}
