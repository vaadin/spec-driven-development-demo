package com.example.specdriven.show;

import com.example.specdriven.movie.MovieEndpoint;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ViewShowtimesTest {

    @Autowired
    private ShowEndpoint showEndpoint;

    @Autowired
    private MovieEndpoint movieEndpoint;

    @Test
    void showtimesAreGroupedByDate() {
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        Long movieId = movies.getFirst().id();

        List<ShowEndpoint.ShowtimeGroup> groups = showEndpoint.getShowtimesForMovie(movieId);
        assertNotNull(groups);
        assertFalse(groups.isEmpty());

        for (ShowEndpoint.ShowtimeGroup group : groups) {
            assertNotNull(group.date());
            assertFalse(group.showtimes().isEmpty());
        }
    }

    @Test
    void eachShowtimeDisplaysTimeRoomAndAvailableSeats() {
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        Long movieId = movies.getFirst().id();

        List<ShowEndpoint.ShowtimeGroup> groups = showEndpoint.getShowtimesForMovie(movieId);
        ShowEndpoint.ShowtimeInfo info = groups.getFirst().showtimes().getFirst();

        assertNotNull(info.id());
        assertNotNull(info.time());
        assertNotNull(info.roomName());
        assertTrue(info.totalSeats() > 0);
        assertTrue(info.availableSeats() >= 0);
    }

    @Test
    void showDetailReturnsCorrectInfo() {
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        Long movieId = movies.getFirst().id();

        List<ShowEndpoint.ShowtimeGroup> groups = showEndpoint.getShowtimesForMovie(movieId);
        Long showId = groups.getFirst().showtimes().getFirst().id();

        ShowEndpoint.ShowDetail detail = showEndpoint.getShowDetail(showId);
        assertNotNull(detail);
        assertNotNull(detail.movieTitle());
        assertNotNull(detail.roomName());
        assertTrue(detail.rows() > 0);
        assertTrue(detail.seatsPerRow() > 0);
    }
}
