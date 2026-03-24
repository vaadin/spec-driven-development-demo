package com.example.specdriven.movie;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import java.util.List;

@BrowserCallable
@AnonymousAllowed
public class MovieEndpoint {

    private final MovieService movieService;

    public MovieEndpoint(MovieService movieService) {
        this.movieService = movieService;
    }

    public record MovieSummary(Long id, String title, int durationMinutes, String posterFileName) {}

    public record MovieDetail(Long id, String title, String description, int durationMinutes, String posterFileName) {}

    public List<MovieSummary> getMoviesWithFutureShows() {
        return movieService.findMoviesWithFutureShows().stream()
                .map(m -> new MovieSummary(m.getId(), m.getTitle(), m.getDurationMinutes(), m.getPosterFileName()))
                .toList();
    }

    public MovieDetail getMovieById(Long id) {
        Movie m = movieService.findById(id);
        if (m == null) return null;
        return new MovieDetail(m.getId(), m.getTitle(), m.getDescription(), m.getDurationMinutes(), m.getPosterFileName());
    }
}
