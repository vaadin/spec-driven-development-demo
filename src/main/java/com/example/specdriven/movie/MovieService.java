package com.example.specdriven.movie;

import com.example.specdriven.show.ShowRepository;
import com.example.specdriven.ticket.TicketRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final TicketRepository ticketRepository;

    public MovieService(MovieRepository movieRepository, ShowRepository showRepository,
            TicketRepository ticketRepository) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public List<Movie> findMoviesWithFutureShows() {
        return movieRepository.findMoviesWithFutureShows();
    }

    public Movie findById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    public long countShows(Long movieId) {
        return showRepository.countByMovieId(movieId);
    }

    @Transactional
    public Movie save(Movie movie) {
        if (movie.getId() == null) {
            if (movieRepository.existsByTitle(movie.getTitle())) {
                throw new IllegalArgumentException("A movie with this title already exists");
            }
        } else {
            if (movieRepository.existsByTitleAndIdNot(movie.getTitle(), movie.getId())) {
                throw new IllegalArgumentException("A movie with this title already exists");
            }
        }
        return movieRepository.save(movie);
    }

    @Transactional
    public void delete(Long movieId) {
        long futureTickets = ticketRepository.countFutureTicketsByMovieId(movieId);
        if (futureTickets > 0) {
            throw new IllegalStateException("Cannot delete: movie has future shows with sold tickets");
        }
        movieRepository.deleteById(movieId);
    }

    public void savePoster(String fileName, InputStream inputStream) throws IOException {
        Path postersDir = Path.of("posters");
        Files.createDirectories(postersDir);
        Files.copy(inputStream, postersDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    }
}
