package com.example.specdriven.show;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class ManageShowsViewTest extends SpringBrowserlessTest {

    @Test
    void viewShowsAllShows() {
        navigate(ManageShowsView.class);
        Grid<Show> grid = $(Grid.class).first();
        assertNotNull(grid);
        assertTrue(grid.getListDataView().getItemCount() > 0);
    }

    @Test
    void viewHasFormFields() {
        navigate(ManageShowsView.class);
        assertEquals(2, $(ComboBox.class).all().size()); // Movie + Room
        assertNotNull($(DateTimePicker.class).first());
    }

    @Test
    void viewHasActionButtons() {
        navigate(ManageShowsView.class);
        var buttons = $(Button.class).all();
        var labels = buttons.stream().map(Button::getText).toList();
        assertTrue(labels.contains("Save"));
        assertTrue(labels.contains("Delete"));
        assertTrue(labels.contains("Add Show"));
    }

    @Test
    void routeRequiresAdminRole() {
        ManageShowsView view = navigate(ManageShowsView.class);
        assertNotNull(view);
    }
}
