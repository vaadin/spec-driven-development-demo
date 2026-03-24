package com.example.specdriven.task;

import com.example.specdriven.member.Member;
import com.example.specdriven.member.MemberRepository;
import com.example.specdriven.project.Project;
import com.example.specdriven.project.ProjectService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Route("projects/:projectId/tasks")
@PageTitle("Tasks | Forge")
@PermitAll
public class TaskManagementView extends VerticalLayout implements BeforeEnterObserver {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final MemberRepository memberRepository;
    private final boolean isAdmin;

    private Long projectId;
    private Project project;
    Grid<Task> grid;
    private Select<TaskStatus> statusFilter;
    private ComboBox<Member> assigneeFilter;

    public TaskManagementView(TaskService taskService, ProjectService projectService,
                               MemberRepository memberRepository) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.memberRepository = memberRepository;
        this.isAdmin = hasAdminRole();

        setPadding(true);
        setSpacing(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String paramStr = event.getRouteParameters().get("projectId").orElse(null);
        if (paramStr == null) {
            event.forwardTo("");
            return;
        }
        try {
            projectId = Long.valueOf(paramStr);
        } catch (NumberFormatException e) {
            event.forwardTo("");
            return;
        }
        project = projectService.findById(projectId);
        if (project == null) {
            event.forwardTo("");
            return;
        }
        buildView();
    }

    private void buildView() {
        removeAll();

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H2 title = new H2(project.getName() + " — Tasks");

        HorizontalLayout actions = new HorizontalLayout();
        actions.setAlignItems(FlexComponent.Alignment.CENTER);

        if (isAdmin) {
            Button addTaskBtn = new Button("Add Task", e -> openTaskDialog(null));
            addTaskBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            actions.add(addTaskBtn);
        }

        Button ganttBtn = new Button("Gantt Chart",
                e -> UI.getCurrent().navigate("projects/" + projectId + "/gantt"));
        actions.add(ganttBtn);

        Button backBtn = new Button("Back to Projects",
                e -> UI.getCurrent().navigate("projects"));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        actions.add(backBtn);

        header.add(title, actions);
        add(header);

        // Filters
        HorizontalLayout filters = new HorizontalLayout();
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);

        statusFilter = new Select<>();
        statusFilter.setLabel("Status");
        statusFilter.setItems(TaskStatus.values());
        statusFilter.setEmptySelectionAllowed(true);
        statusFilter.setEmptySelectionCaption("All");
        statusFilter.addValueChangeListener(e -> refreshGrid());

        assigneeFilter = new ComboBox<>("Assignee");
        assigneeFilter.setItems(memberRepository.findAll());
        assigneeFilter.setItemLabelGenerator(Member::getName);
        assigneeFilter.setClearButtonVisible(true);
        assigneeFilter.addValueChangeListener(e -> refreshGrid());

        filters.add(statusFilter, assigneeFilter);
        add(filters);

        // Grid
        grid = new Grid<>(Task.class, false);
        grid.addColumn(Task::getName).setHeader("Name").setSortable(true).setFlexGrow(2);
        grid.addColumn(task -> task.getAssignee() != null ? task.getAssignee().getName() : "—")
                .setHeader("Assignee").setSortable(true);
        grid.addColumn(Task::getStartDate).setHeader("Start Date").setSortable(true);
        grid.addColumn(Task::getEndDate).setHeader("End Date").setSortable(true);
        grid.addComponentColumn(task -> createPriorityBadge(task.getPriority()))
                .setHeader("Priority").setSortable(true);
        grid.addComponentColumn(task -> createStatusBadge(task.getStatus()))
                .setHeader("Status").setSortable(true);

        grid.addItemClickListener(e -> openTaskDialog(e.getItem()));
        grid.setWidthFull();
        grid.setHeight("600px");

        add(grid);
        refreshGrid();
    }

    void refreshGrid() {
        List<Task> tasks = taskService.findByProjectId(projectId);

        TaskStatus statusValue = statusFilter.getValue();
        if (statusValue != null) {
            tasks = tasks.stream().filter(t -> t.getStatus() == statusValue).toList();
        }

        Member assigneeValue = assigneeFilter.getValue();
        if (assigneeValue != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getAssignee() != null && t.getAssignee().getId().equals(assigneeValue.getId()))
                    .toList();
        }

        grid.setItems(tasks);
    }

    void openTaskDialog(Task existingTask) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(existingTask == null ? "Add Task" : "Edit Task");
        dialog.setWidth("520px");

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

        Select<TaskPriority> priorityField = new Select<>();
        priorityField.setLabel("Priority");
        priorityField.setItems(TaskPriority.values());
        priorityField.setWidthFull();

        Select<TaskStatus> statusField = new Select<>();
        statusField.setLabel("Status");
        statusField.setItems(TaskStatus.values());
        statusField.setWidthFull();

        ComboBox<Member> assigneeField = new ComboBox<>("Assignee");
        assigneeField.setItems(memberRepository.findAll());
        assigneeField.setItemLabelGenerator(Member::getName);
        assigneeField.setClearButtonVisible(true);
        assigneeField.setWidthFull();

        if (existingTask != null) {
            nameField.setValue(existingTask.getName());
            descriptionField.setValue(existingTask.getDescription() != null ? existingTask.getDescription() : "");
            startDateField.setValue(existingTask.getStartDate());
            endDateField.setValue(existingTask.getEndDate());
            priorityField.setValue(existingTask.getPriority());
            statusField.setValue(existingTask.getStatus());
            assigneeField.setValue(existingTask.getAssignee());

            if (!isAdmin) {
                nameField.setReadOnly(true);
                descriptionField.setReadOnly(true);
                startDateField.setReadOnly(true);
                endDateField.setReadOnly(true);
                priorityField.setReadOnly(true);
                assigneeField.setReadOnly(true);
            }
        } else {
            priorityField.setValue(TaskPriority.MEDIUM);
            statusField.setValue(TaskStatus.TODO);
        }

        VerticalLayout formLayout = new VerticalLayout(
                nameField, descriptionField, startDateField, endDateField,
                priorityField, statusField, assigneeField);
        formLayout.setPadding(false);
        formLayout.setSpacing(true);
        dialog.add(formLayout);

        Button saveBtn = new Button("Save", e -> {
            try {
                Task task = existingTask != null ? existingTask : new Task();
                if (existingTask == null || isAdmin) {
                    task.setName(nameField.getValue());
                    task.setDescription(descriptionField.getValue());
                    task.setStartDate(startDateField.getValue());
                    task.setEndDate(endDateField.getValue());
                    task.setPriority(priorityField.getValue());
                    task.setAssignee(assigneeField.getValue());
                }
                task.setStatus(statusField.getValue());
                if (existingTask == null) {
                    task.setProject(project);
                }
                taskService.save(task);
                dialog.close();
                refreshGrid();
                Notification.show("Task saved", 3000, Notification.Position.BOTTOM_STRETCH)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage(), 3000, Notification.Position.BOTTOM_STRETCH)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancel", e -> dialog.close());

        HorizontalLayout footer = new HorizontalLayout(cancelBtn, saveBtn);

        if (existingTask != null && isAdmin) {
            Button deleteBtn = new Button("Delete", e -> {
                dialog.close();
                confirmDeleteTask(existingTask);
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            footer.addComponentAtIndex(0, deleteBtn);
        }

        dialog.getFooter().add(footer);
        dialog.open();
    }

    private void confirmDeleteTask(Task task) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Task");
        confirm.setText("Are you sure you want to delete \"" + task.getName() + "\"?");
        confirm.setCancelable(true);
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");
        confirm.addConfirmListener(e -> {
            taskService.delete(task.getId());
            refreshGrid();
            Notification.show("Task deleted", 3000, Notification.Position.BOTTOM_STRETCH)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        confirm.open();
    }

    private Span createStatusBadge(TaskStatus status) {
        Span badge = new Span(status.name().replace('_', ' '));
        badge.getStyle()
                .set("padding", "2px 8px")
                .set("border-radius", "4px")
                .set("font-size", "var(--aura-font-size-s)")
                .set("font-weight", "500");
        switch (status) {
            case TODO -> badge.getStyle().set("background", "#EEEEEE").set("color", "#616161");
            case IN_PROGRESS -> badge.getStyle().set("background", "#FFF3E0").set("color", "#E65100");
            case DONE -> badge.getStyle().set("background", "#E8F5E9").set("color", "#2E7D32");
        }
        return badge;
    }

    private Span createPriorityBadge(TaskPriority priority) {
        Span badge = new Span(priority.name());
        badge.getStyle()
                .set("padding", "2px 8px")
                .set("border-radius", "4px")
                .set("font-size", "var(--aura-font-size-s)")
                .set("font-weight", "500");
        switch (priority) {
            case LOW -> badge.getStyle().set("background", "#EEEEEE").set("color", "#616161");
            case MEDIUM -> badge.getStyle().set("background", "#FFF3E0").set("color", "#E65100");
            case HIGH -> badge.getStyle().set("background", "#FFEBEE").set("color", "#C62828");
        }
        return badge;
    }

    private boolean hasAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
