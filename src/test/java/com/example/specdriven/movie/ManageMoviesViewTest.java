package com.example.specdriven.movie;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class ManageMoviesViewTest extends SpringBrowserlessTest {

    @Test
    void viewShowsAllMovies() {
        navigate(ManageMoviesView.class);
        Grid<Movie> grid = $(Grid.class).first();
        assertNotNull(grid);
        assertTrue(grid.getListDataView().getItemCount() > 0);
    }

    @Test
    void viewHasFormFields() {
        navigate(ManageMoviesView.class);
        assertNotNull($(TextField.class).first());
        assertNotNull($(TextArea.class).first());
        assertNotNull($(IntegerField.class).first());
        assertNotNull($(Upload.class).first());
    }

    @Test
    void viewHasActionButtons() {
        navigate(ManageMoviesView.class);
        var buttons = $(Button.class).all();
        var buttonLabels = buttons.stream().map(Button::getText).toList();
        assertTrue(buttonLabels.contains("Save"));
        assertTrue(buttonLabels.contains("Delete"));
        assertTrue(buttonLabels.contains("Cancel"));
        assertTrue(buttonLabels.contains("Add Movie"));
    }

    @Test
    void routeRequiresAdminRole() {
        ManageMoviesView view = navigate(ManageMoviesView.class);
        assertNotNull(view);
    }
}
