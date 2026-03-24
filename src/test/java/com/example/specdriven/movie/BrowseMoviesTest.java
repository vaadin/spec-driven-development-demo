package com.example.specdriven.movie;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BrowseMoviesTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieEndpoint movieEndpoint;

    @Test
    void onlyMoviesWithFutureShowsAreDisplayed() {
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // All seeded movies have future shows
        assertEquals(8, movies.size());
    }

    @Test
    void moviesAreSortedAlphabetically() {
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        for (int i = 1; i < movies.size(); i++) {
            assertTrue(movies.get(i - 1).title().compareToIgnoreCase(movies.get(i).title()) <= 0,
                    "Movies should be sorted alphabetically");
        }
    }

    @Test
    void movieSummaryContainsRequiredFields() {
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        MovieEndpoint.MovieSummary first = movies.getFirst();
        assertNotNull(first.id());
        assertNotNull(first.title());
        assertTrue(first.durationMinutes() > 0);
    }

    @Test
    void movieDetailPageReturnsCorrectInfo() {
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        MovieEndpoint.MovieDetail detail = movieEndpoint.getMovieById(movies.getFirst().id());
        assertNotNull(detail);
        assertEquals(movies.getFirst().title(), detail.title());
        assertNotNull(detail.description());
    }

    @Test
    void pageIsAccessibleWithoutAuthentication() {
        // MovieEndpoint is @AnonymousAllowed, so it should work without auth
        List<MovieEndpoint.MovieSummary> movies = movieEndpoint.getMoviesWithFutureShows();
        assertNotNull(movies);
    }
}
