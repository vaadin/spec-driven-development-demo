package com.example.specdriven.admin;

import com.example.specdriven.data.Movie;
import com.example.specdriven.data.MovieRepository;
import com.example.specdriven.data.Screening;
import com.example.specdriven.data.ScreeningRepository;
import com.example.specdriven.security.LoginView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class MoviesAdminViewTest extends SpringUIUnitTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @BeforeEach
    void setUp() {
        screeningRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void gridShowsAllMovies() {
        movieRepository.save(new Movie("Inception", "A mind-bending thriller", "inception.png", 148));
        movieRepository.save(new Movie("The Matrix", "A sci-fi classic", "matrix.png", 136));

        navigate(MoviesAdminView.class);

        @SuppressWarnings("unchecked")
        Grid<Movie> grid = $(Grid.class).first();
        assertEquals(2, grid.getGenericDataView().getItems().count());
    }

    @Test
    void addNewMovie() {
        navigate(MoviesAdminView.class);

        // Fill in form fields
        test($(TextField.class).withPropertyValue(TextField::getLabel, "Title").first())
                .setValue("New Movie");
        test($(TextArea.class).first())
                .setValue("A great description");
        test($(TextField.class).withPropertyValue(TextField::getLabel, "Poster Filename").first())
                .setValue("new-movie.png");
        test($(IntegerField.class).first())
                .setValue(120);

        // Click Save
        test($(Button.class).withPropertyValue(Button::getText, "Save").first())
                .click();

        // Verify success notification
        Notification notification = $(Notification.class).first();
        assertNotNull(notification);
        assertEquals("Movie saved.", test(notification).getText());

        // Verify movie persisted
        assertEquals(1, movieRepository.count());
        Movie saved = movieRepository.findAll().getFirst();
        assertEquals("New Movie", saved.getTitle());
        assertEquals("A great description", saved.getDescription());
        assertEquals("new-movie.png", saved.getPosterFileName());
        assertEquals(120, saved.getDurationMinutes());
    }

    @Test
    void editExistingMovie() {
        Movie movie = movieRepository.save(new Movie("Old Title", "Old desc", "old.png", 90));

        navigate(MoviesAdminView.class);

        // Select movie in grid
        @SuppressWarnings("unchecked")
        Grid<Movie> grid = $(Grid.class).first();
        grid.asSingleSelect().setValue(movie);

        // Modify title
        test($(TextField.class).withPropertyValue(TextField::getLabel, "Title").first())
                .setValue("Updated Title");

        // Click Save
        test($(Button.class).withPropertyValue(Button::getText, "Save").first())
                .click();

        // Verify update persisted
        Movie updated = movieRepository.findById(movie.getId()).orElseThrow();
        assertEquals("Updated Title", updated.getTitle());
    }

    @Test
    void deleteMovieWithoutShows() {
        Movie movie = movieRepository.save(new Movie("To Delete", "Desc", "poster.png", 100));

        navigate(MoviesAdminView.class);

        // Select movie in grid
        @SuppressWarnings("unchecked")
        Grid<Movie> grid = $(Grid.class).first();
        grid.asSingleSelect().setValue(movie);

        // Click Delete
        test($(Button.class).withPropertyValue(Button::getText, "Delete").first())
                .click();

        // Confirm deletion in dialog
        ConfirmDialog dialog = $(ConfirmDialog.class).first();
        assertNotNull(dialog);
        test(dialog).confirm();

        // Verify movie removed
        assertEquals(0, movieRepository.count());
    }

    @Test
    void deleteMovieWithFutureShowsPrevented() {
        Movie movie = movieRepository.save(new Movie("Has Shows", "Desc", "poster.png", 100));
        screeningRepository.save(new Screening(movie, LocalDateTime.now().plusDays(1), 120));

        navigate(MoviesAdminView.class);

        // Select movie in grid
        @SuppressWarnings("unchecked")
        Grid<Movie> grid = $(Grid.class).first();
        grid.asSingleSelect().setValue(movie);

        // Click Delete
        test($(Button.class).withPropertyValue(Button::getText, "Delete").first())
                .click();

        // Confirm deletion in dialog
        ConfirmDialog dialog = $(ConfirmDialog.class).first();
        test(dialog).confirm();

        // Verify error notification shown
        Notification notification = $(Notification.class).first();
        assertNotNull(notification);
        assertTrue(test(notification).getText().contains("future screening"));

        // Verify movie NOT removed
        assertEquals(1, movieRepository.count());
    }

    @Test
    void validationPreventsEmptySave() {
        navigate(MoviesAdminView.class);

        // Clear any default values and try to save with empty title
        test($(TextField.class).withPropertyValue(TextField::getLabel, "Title").first())
                .setValue("");

        // Click Save
        test($(Button.class).withPropertyValue(Button::getText, "Save").first())
                .click();

        // Verify no movie was saved
        assertEquals(0, movieRepository.count());
    }

    @Test
    @WithAnonymousUser
    void anonymousUserRedirectedToLogin() {
        navigate("admin/movies", LoginView.class);
    }
}
