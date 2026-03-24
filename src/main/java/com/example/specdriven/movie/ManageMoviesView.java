package com.example.specdriven.movie;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("admin/movies")
@PageTitle("Manage Movies — CineMax")
@RolesAllowed("ADMIN")
public class ManageMoviesView extends VerticalLayout {

    private final MovieService movieService;
    private final Grid<Movie> grid = new Grid<>(Movie.class, false);
    private final TextField titleField = new TextField("Title");
    private final TextArea descriptionField = new TextArea("Description");
    private final IntegerField durationField = new IntegerField("Duration (minutes)");
    private final Upload posterUpload;

    private Movie currentMovie;
    private String uploadedFileName;
    private byte[] uploadedFileData;

    public ManageMoviesView(MovieService movieService) {
        this.movieService = movieService;
        setSizeFull();
        setPadding(true);

        add(new H2("Manage Movies"));

        configureGrid();

        var memoryBuffer = new MemoryBuffer();
        posterUpload = new Upload(memoryBuffer);
        posterUpload.setAcceptedFileTypes("image/png", "image/jpeg");
        posterUpload.setMaxFileSize(2 * 1024 * 1024);
        posterUpload.addSucceededListener(event -> {
            uploadedFileName = event.getFileName();
            try {
                uploadedFileData = memoryBuffer.getInputStream().readAllBytes();
            } catch (Exception ex) {
                Notification.show("Failed to read uploaded file", 5000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.ERROR);
            }
        });

        var form = createForm();
        var splitLayout = new SplitLayout(grid, form);
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(60);
        add(splitLayout);

        refreshGrid();
    }

    private void configureGrid() {
        grid.addColumn(Movie::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(Movie::getDurationMinutes).setHeader("Duration (min)").setSortable(true);
        grid.addColumn(movie -> movieService.countShows(movie.getId())).setHeader("Shows");
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                editMovie(event.getValue());
            } else {
                clearForm();
            }
        });
    }

    private VerticalLayout createForm() {
        titleField.setRequired(true);
        titleField.setMaxLength(200);
        titleField.setWidthFull();

        descriptionField.setMaxLength(2000);
        descriptionField.setWidthFull();

        durationField.setMin(1);
        durationField.setWidthFull();

        var formLayout = new FormLayout();
        formLayout.add(titleField, descriptionField, durationField, posterUpload);
        formLayout.setColspan(descriptionField, 2);
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

        var addButton = new Button("Add Movie", e -> {
            grid.asSingleSelect().clear();
            clearForm();
            currentMovie = new Movie();
        });

        var buttons = new HorizontalLayout(saveButton, deleteButton, cancelButton);
        buttons.setSpacing(true);

        var formContainer = new VerticalLayout(addButton, formLayout, buttons);
        formContainer.setPadding(true);
        formContainer.setSpacing(true);
        return formContainer;
    }

    private void editMovie(Movie movie) {
        currentMovie = movie;
        titleField.setValue(movie.getTitle() != null ? movie.getTitle() : "");
        descriptionField.setValue(movie.getDescription() != null ? movie.getDescription() : "");
        durationField.setValue(movie.getDurationMinutes());
        uploadedFileName = null;
        uploadedFileData = null;
    }

    private void clearForm() {
        currentMovie = null;
        titleField.clear();
        descriptionField.clear();
        durationField.clear();
        uploadedFileName = null;
        uploadedFileData = null;
    }

    private void save() {
        if (currentMovie == null) {
            Notification.show("Click 'Add Movie' or select a movie to edit", 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        String title = titleField.getValue();
        if (title == null || title.isBlank()) {
            Notification.show("Title is required", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.ERROR);
            return;
        }

        Integer duration = durationField.getValue();
        if (duration == null || duration <= 0) {
            Notification.show("Duration must be a positive number", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.ERROR);
            return;
        }

        currentMovie.setTitle(title);
        currentMovie.setDescription(descriptionField.getValue());
        currentMovie.setDurationMinutes(duration);

        if (uploadedFileName != null && uploadedFileData != null) {
            try {
                movieService.savePoster(uploadedFileName, new java.io.ByteArrayInputStream(uploadedFileData));
                currentMovie.setPosterFileName(uploadedFileName);
            } catch (Exception ex) {
                Notification.show("Failed to save poster: " + ex.getMessage(), 5000,
                        Notification.Position.TOP_CENTER).addThemeVariants(NotificationVariant.ERROR);
                return;
            }
        }

        try {
            movieService.save(currentMovie);
            Notification.show("Movie saved", 3000, Notification.Position.TOP_CENTER);
            clearForm();
            grid.asSingleSelect().clear();
            refreshGrid();
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.ERROR);
        }
    }

    private void delete() {
        if (currentMovie == null || currentMovie.getId() == null) {
            return;
        }
        try {
            movieService.delete(currentMovie.getId());
            Notification.show("Movie deleted", 3000, Notification.Position.TOP_CENTER);
            clearForm();
            grid.asSingleSelect().clear();
            refreshGrid();
        } catch (IllegalStateException e) {
            Notification.show(e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.ERROR);
        }
    }

    private void refreshGrid() {
        grid.setItems(movieService.findAll());
    }
}
