package com.example.specdriven.project;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("projects")
@RouteAlias("")
@PageTitle("Projects | Forge")
@PermitAll
public class ProjectDashboardView extends VerticalLayout {

    private final ProjectService projectService;
    private final FlexLayout cardGrid = new FlexLayout();
    private final boolean isAdmin;

    public ProjectDashboardView(ProjectService projectService) {
        this.projectService = projectService;
        this.isAdmin = hasAdminRole();

        setPadding(true);
        setSpacing(true);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H2 title = new H2("Projects");
        header.add(title);

        if (isAdmin) {
            Button newProjectBtn = new Button("New Project", e -> openCreateDialog());
            newProjectBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            header.add(newProjectBtn);
        }

        add(header);

        cardGrid.addClassName("card-grid");
        add(cardGrid);

        refreshProjects();
    }

    void refreshProjects() {
        cardGrid.removeAll();
        for (Project project : projectService.findAll()) {
            cardGrid.add(createProjectCard(project));
        }
    }

    private Div createProjectCard(Project project) {
        Div card = new Div();
        card.addClassName("project-card");

        H3 name = new H3(project.getName());
        name.getStyle().set("margin", "0 0 var(--vaadin-space-xs) 0");

        Span dateRange = new Span(project.getStartDate() + " — " + project.getEndDate());
        dateRange.getStyle().set("color", "#757575").set("font-size", "var(--aura-font-size-s)");

        int progress = projectService.computeProgressPercent(project.getId());
        ProjectStatus status = projectService.computeStatus(project.getId());

        ProgressBar progressBar = new ProgressBar(0, 100, progress);
        progressBar.setWidthFull();
        progressBar.getStyle()
                .set("margin", "var(--vaadin-space-s) 0")
                .set("--vaadin-progress-bar-height", "8px");

        Span progressLabel = new Span(progress + "% complete");
        progressLabel.getStyle().set("font-size", "var(--aura-font-size-s)").set("color", "#757575");

        Span statusBadge = createStatusBadge(status);

        HorizontalLayout bottomRow = new HorizontalLayout(progressLabel, statusBadge);
        bottomRow.setWidthFull();
        bottomRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        bottomRow.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout cardContent = new VerticalLayout(name, dateRange, progressBar, bottomRow);
        cardContent.setPadding(false);
        cardContent.setSpacing(false);
        cardContent.setWidthFull();
        cardContent.getStyle().set("gap", "var(--vaadin-space-s)");

        card.add(cardContent);

        if (isAdmin) {
            Button deleteBtn = new Button("Delete");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            deleteBtn.addClickListener(e -> {
                e.getSource().getElement().executeJs("arguments[0].stopPropagation()", e.getSource().getElement());
                confirmDelete(project);
            });
            deleteBtn.getElement().addEventListener("click", domEvent -> {}).addEventData("event.stopPropagation()");
            bottomRow.add(deleteBtn);
        }

        card.addClickListener(e -> UI.getCurrent().navigate("projects/" + project.getId() + "/tasks"));

        return card;
    }

    private Span createStatusBadge(ProjectStatus status) {
        Span badge = new Span(status.name().replace('_', ' '));
        badge.getStyle()
                .set("padding", "2px 8px")
                .set("border-radius", "var(--vaadin-input-field-border-radius, 4px)")
                .set("font-size", "var(--aura-font-size-s)")
                .set("font-weight", "500")
                .set("white-space", "nowrap");

        switch (status) {
            case NOT_STARTED -> badge.getStyle()
                    .set("background", "#EEEEEE")
                    .set("color", "#616161");
            case IN_PROGRESS -> badge.getStyle()
                    .set("background", "#FFF3E0")
                    .set("color", "#E65100");
            case COMPLETED -> badge.getStyle()
                    .set("background", "#E8F5E9")
                    .set("color", "#2E7D32");
        }
        return badge;
    }

    void openCreateDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Project");
        dialog.setWidth("480px");

        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextArea descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();

        DatePicker startDateField = new DatePicker("Start Date");
        startDateField.setWidthFull();
        startDateField.setRequired(true);

        DatePicker endDateField = new DatePicker("End Date");
        endDateField.setWidthFull();
        endDateField.setRequired(true);

        VerticalLayout formLayout = new VerticalLayout(nameField, descriptionField, startDateField, endDateField);
        formLayout.setPadding(false);
        formLayout.setSpacing(true);
        dialog.add(formLayout);

        Button saveBtn = new Button("Save", e -> {
            try {
                Project project = new Project(
                        nameField.getValue(),
                        descriptionField.getValue(),
                        startDateField.getValue(),
                        endDateField.getValue()
                );
                projectService.save(project);
                dialog.close();
                refreshProjects();
                Notification.show("Project created", 3000, Notification.Position.BOTTOM_STRETCH)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage(), 3000, Notification.Position.BOTTOM_STRETCH)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());

        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private void confirmDelete(Project project) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Project");
        dialog.setText("Are you sure you want to delete \"" + project.getName() + "\"? This will also delete all tasks and dependencies.");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            projectService.delete(project.getId());
            refreshProjects();
            Notification.show("Project deleted", 3000, Notification.Position.BOTTOM_STRETCH)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        dialog.open();
    }

    private boolean hasAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
