package com.example.specdriven.movie;

import com.example.specdriven.data.Screening;
import com.example.specdriven.data.ScreeningRepository;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@BrowserCallable
@AnonymousAllowed
public class MovieBrowseService {

    private final ScreeningRepository screeningRepository;

    public MovieBrowseService(ScreeningRepository screeningRepository) {
        this.screeningRepository = screeningRepository;
    }

    public List<MovieWithScreeningsDTO> getTodaysMovies() {
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Screening> screenings = screeningRepository.findScreeningsByDate(dayStart, dayEnd);

        Map<Long, MovieWithScreeningsDTO> movieMap = new LinkedHashMap<>();
        for (Screening screening : screenings) {
            var movie = screening.getMovie();
            MovieWithScreeningsDTO dto = movieMap.computeIfAbsent(movie.getId(),
                    id -> new MovieWithScreeningsDTO(
                            movie.getId(),
                            movie.getTitle(),
                            movie.getPosterFileName(),
                            new ArrayList<>()));

            dto.screenings().add(new ScreeningDTO(
                    screening.getId(),
                    screening.getStartTime().toLocalTime(),
                    screening.getStartTime().isBefore(now)));
        }

        return new ArrayList<>(movieMap.values());
    }

    public record MovieWithScreeningsDTO(
            long id,
            String title,
            String posterFileName,
            List<ScreeningDTO> screenings) {
    }

    public record ScreeningDTO(
            long id,
            LocalTime time,
            boolean past) {
    }
}
