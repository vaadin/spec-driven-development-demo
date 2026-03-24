package com.example.specdriven.show;

import com.example.specdriven.movie.Movie;
import com.example.specdriven.room.ScreeningRoom;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.time.LocalDateTime;

@Route("admin/shows")
@PageTitle("Manage Shows — CineMax")
@RolesAllowed("ADMIN")
public class ManageShowsView extends VerticalLayout {

    private final ShowService showService;
    private final Grid<Show> grid = new Grid<>(Show.class, false);
    private final ComboBox<Movie> movieCombo = new ComboBox<>("Movie");
    private final ComboBox<ScreeningRoom> roomCombo = new ComboBox<>("Screening Room");
    private final DateTimePicker dateTimePicker = new DateTimePicker("Date & Time");

    private Show currentShow;

    public ManageShowsView(ShowService showService) {
        this.showService = showService;
        setSizeFull();
        setPadding(true);

        add(new H2("Manage Shows"));

        configureGrid();
        configureForm();

        var form = createForm();
        var splitLayout = new SplitLayout(grid, form);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(60);
        add(splitLayout);

        refreshGrid();
    }

    private void configureGrid() {
        grid.addColumn(show -> show.getMovie().getTitle()).setHeader("Movie").setSortable(true);
        grid.addColumn(show -> show.getDateTime().toString()).setHeader("Date/Time").setSortable(true);
        grid.addColumn(show -> show.getScreeningRoom().getName()).setHeader("Room").setSortable(true);
        grid.addColumn(show -> {
            long sold = showService.countTickets(show.getId());
            int total = showService.totalCapacity(show);
            return sold + " / " + total;
        }).setHeader("Tickets");
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                editShow(event.getValue());
            } else {
                clearForm();
            }
        });
    }

    private void configureForm() {
        movieCombo.setItems(showService.findAllMovies());
        movieCombo.setItemLabelGenerator(Movie::getTitle);
        movieCombo.setWidthFull();
        movieCombo.setRequired(true);

        roomCombo.setItems(showService.findAllRooms());
        roomCombo.setItemLabelGenerator(ScreeningRoom::getName);
        roomCombo.setWidthFull();
        roomCombo.setRequired(true);

        dateTimePicker.setWidthFull();
    }

    private VerticalLayout createForm() {
        var formLayout = new FormLayout();
        formLayout.add(movieCombo, roomCombo, dateTimePicker);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        var saveButton = new Button("Save", e -> save());
        saveButton.addThemeVariants(ButtonVariant.PRIMARY);

        var deleteButton = new Button("Delete", e -> delete());
        deleteButton.addThemeVariants(ButtonVariant.ERROR);

        var cancelButton = new Button("Cancel", e -> {
            clearForm();
            grid.asSingleSelect().clear();
        });

        var addButton = new Button("Add Show", e -> {
            grid.asSingleSelect().clear();
            clearForm();
            currentShow = new Show();
            movieCombo.setEnabled(true);
        });

        var buttons = new HorizontalLayout(saveButton, deleteButton, cancelButton);
        buttons.setSpacing(true);

        var formContainer = new VerticalLayout(addButton, formLayout, buttons);
        formContainer.setPadding(true);
        formContainer.setSpacing(true);
        return formContainer;
    }

    private void editShow(Show show) {
        currentShow = show;
        movieCombo.setValue(show.getMovie());
        movieCombo.setEnabled(false); // BR-04: movie cannot be changed
        roomCombo.setValue(show.getScreeningRoom());
        dateTimePicker.setValue(show.getDateTime());
    }

    private void clearForm() {
        currentShow = null;
        movieCombo.clear();
        movieCombo.setEnabled(true);
        roomCombo.clear();
        dateTimePicker.clear();
    }

    private void save() {
        if (currentShow == null) {
            Notification.show("Click 'Add Show' or select a show to edit", 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        Movie movie = movieCombo.getValue();
        ScreeningRoom room = roomCombo.getValue();
        LocalDateTime dateTime = dateTimePicker.getValue();

        if (movie == null || room == null || dateTime == null) {
            Notification.show("All fields are required", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.ERROR);
            return;
        }

        if (currentShow.getId() == null) {
            currentShow.setMovie(movie);
        }
        currentShow.setScreeningRoom(room);
        currentShow.setDateTime(dateTime);

        try {
            showService.save(currentShow);
            Notification.show("Show saved", 3000, Notification.Position.TOP_CENTER);
            clearForm();
            grid.asSingleSelect().clear();
            refreshGrid();
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.ERROR);
        }
    }

    private void delete() {
        if (currentShow == null || currentShow.getId() == null) {
            return;
        }
        try {
            showService.delete(currentShow.getId());
            Notification.show("Show deleted", 3000, Notification.Position.TOP_CENTER);
            clearForm();
            grid.asSingleSelect().clear();
            refreshGrid();
        } catch (IllegalStateException e) {
            Notification.show(e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.ERROR);
        }
    }

    private void refreshGrid() {
        grid.setItems(showService.findAll());
    }
}
