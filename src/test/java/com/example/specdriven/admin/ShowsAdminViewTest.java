package com.example.specdriven.admin;

import com.example.specdriven.data.Booking;
import com.example.specdriven.data.BookingRepository;
import com.example.specdriven.data.Movie;
import com.example.specdriven.data.MovieRepository;
import com.example.specdriven.data.Screening;
import com.example.specdriven.data.ScreeningRepository;
import com.example.specdriven.security.LoginView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class ShowsAdminViewTest extends SpringUIUnitTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        screeningRepository.deleteAll();
        movieRepository.deleteAll();
        testMovie = movieRepository.save(new Movie("Test Movie", "Description", "poster.png", 120));
    }

    @Test
    void gridShowsAllShows() {
        screeningRepository.save(new Screening(testMovie, LocalDateTime.of(2026, 3, 1, 14, 0), 100));
        screeningRepository.save(new Screening(testMovie, LocalDateTime.of(2026, 3, 1, 18, 0), 80));

        navigate(ShowsAdminView.class);

        @SuppressWarnings("unchecked")
        Grid<Screening> grid = $(Grid.class).first();
        assertEquals(2, grid.getGenericDataView().getItems().count());
    }

    @Test
    void addNewShow() {
        navigate(ShowsAdminView.class);

        // Fill in form fields
        test($(ComboBox.class).first()).selectItem("Test Movie");
        test($(DatePicker.class).first()).setValue(LocalDate.of(2026, 3, 15));
        test($(TimePicker.class).first()).setValue(LocalTime.of(20, 0));
        test($(IntegerField.class).first()).setValue(150);

        // Click Save
        test($(Button.class).withPropertyValue(Button::getText, "Save").first())
                .click();

        // Verify success notification
        Notification notification = $(Notification.class).first();
        assertNotNull(notification);
        assertEquals("Show saved.", test(notification).getText());

        // Verify screening persisted
        assertEquals(1, screeningRepository.count());
        Screening saved = screeningRepository.findAll().getFirst();
        assertEquals("Test Movie", saved.getMovie().getTitle());
        assertEquals(LocalDate.of(2026, 3, 15), saved.getStartTime().toLocalDate());
        assertEquals(LocalTime.of(20, 0), saved.getStartTime().toLocalTime());
        assertEquals(150, saved.getAvailableSeats());
    }

    @Test
    void editExistingShow() {
        Screening screening = screeningRepository.save(
                new Screening(testMovie, LocalDateTime.of(2026, 3, 1, 14, 0), 100));

        navigate(ShowsAdminView.class);

        // Select show in grid
        @SuppressWarnings("unchecked")
        Grid<Screening> grid = $(Grid.class).first();
        grid.asSingleSelect().setValue(screening);

        // Modify available seats
        test($(IntegerField.class).first()).setValue(200);

        // Click Save
        test($(Button.class).withPropertyValue(Button::getText, "Save").first())
                .click();

        // Verify update persisted
        Screening updated = screeningRepository.findById(screening.getId()).orElseThrow();
        assertEquals(200, updated.getAvailableSeats());
    }

    @Test
    void deleteShowWithoutBookings() {
        Screening screening = screeningRepository.save(
                new Screening(testMovie, LocalDateTime.of(2026, 3, 1, 14, 0), 100));

        navigate(ShowsAdminView.class);

        // Select show in grid
        @SuppressWarnings("unchecked")
        Grid<Screening> grid = $(Grid.class).first();
        grid.asSingleSelect().setValue(screening);

        // Click Delete
        test($(Button.class).withPropertyValue(Button::getText, "Delete").first())
                .click();

        // Confirm deletion in dialog
        test($(ConfirmDialog.class).first()).confirm();

        // Verify screening removed
        assertEquals(0, screeningRepository.count());
    }

    @Test
    void deleteShowWithBookingsPrevented() {
        Screening screening = screeningRepository.save(
                new Screening(testMovie, LocalDateTime.of(2026, 3, 1, 14, 0), 100));
        bookingRepository.save(new Booking("CONF-001", "John", "john@test.com", screening, 2));

        navigate(ShowsAdminView.class);

        // Select show in grid
        @SuppressWarnings("unchecked")
        Grid<Screening> grid = $(Grid.class).first();
        grid.asSingleSelect().setValue(screening);

        // Click Delete
        test($(Button.class).withPropertyValue(Button::getText, "Delete").first())
                .click();

        // Confirm deletion in dialog
        test($(ConfirmDialog.class).first()).confirm();

        // Verify error notification
        Notification notification = $(Notification.class).first();
        assertNotNull(notification);
        assertTrue(test(notification).getText().contains("existing booking"));

        // Verify screening NOT removed
        assertEquals(1, screeningRepository.count());
    }

    @Test
    void validationPreventsEmptySave() {
        navigate(ShowsAdminView.class);

        // Try to save with empty form
        test($(Button.class).withPropertyValue(Button::getText, "Save").first())
                .click();

        // Verify no screening was saved
        assertEquals(0, screeningRepository.count());

        // Verify error notification
        Notification notification = $(Notification.class).first();
        assertNotNull(notification);
        assertEquals("Please fill in all fields correctly.", test(notification).getText());
    }

    @Test
    @WithAnonymousUser
    void anonymousUserRedirectedToLogin() {
        navigate("admin/shows", LoginView.class);
    }
}
