package com.example.specdriven.admin;

import com.example.specdriven.data.BookingRepository;
import com.example.specdriven.data.Movie;
import com.example.specdriven.data.MovieRepository;
import com.example.specdriven.data.Screening;
import com.example.specdriven.data.ScreeningRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ShowAdminService {

    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;

    public ShowAdminService(ScreeningRepository screeningRepository,
                            BookingRepository bookingRepository,
                            MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.bookingRepository = bookingRepository;
        this.movieRepository = movieRepository;
    }

    public List<Screening> findAll() {
        return screeningRepository.findAllWithMovie();
    }

    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    public Screening save(Screening screening) {
        return screeningRepository.save(screening);
    }

    public void delete(Screening screening) {
        long bookings = bookingRepository.countByScreening(screening);
        if (bookings > 0) {
            throw new IllegalStateException(
                    "Cannot delete show with " + bookings + " existing booking(s).");
        }
        screeningRepository.delete(screening);
    }
}
