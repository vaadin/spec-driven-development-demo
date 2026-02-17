package com.example.specdriven.admin;

import com.example.specdriven.data.Movie;
import com.example.specdriven.data.MovieRepository;
import com.example.specdriven.data.ScreeningRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MovieAdminService {

    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;

    public MovieAdminService(MovieRepository movieRepository, ScreeningRepository screeningRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public void delete(Movie movie) {
        long futureScreenings = screeningRepository.countByMovieAndStartTimeAfter(movie, LocalDateTime.now());
        if (futureScreenings > 0) {
            throw new IllegalStateException(
                    "Cannot delete movie with " + futureScreenings + " future screening(s).");
        }
        movieRepository.delete(movie);
    }
}
