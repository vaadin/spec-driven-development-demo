package com.example.specdriven.workload;

import com.example.specdriven.task.Task;
import com.example.specdriven.task.TaskStatus;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Map;
import java.util.stream.Collectors;

@Route("team")
@PageTitle("Team Workload | Forge")
@PermitAll
public class TeamWorkloadView extends VerticalLayout {

    private final TeamWorkloadService workloadService;

    public TeamWorkloadView(TeamWorkloadService workloadService) {
        this.workloadService = workloadService;

        setPadding(true);
        setSpacing(true);

        add(new H2("Team Workload"));

        Grid<TeamWorkloadService.MemberWorkload> grid = new Grid<>();
        grid.setWidthFull();
        grid.setHeight("600px");

        grid.addColumn(w -> w.member().getName()).setHeader("Member Name").setSortable(true).setFlexGrow(2);
        grid.addColumn(w -> w.member().getRole().name()).setHeader("Role").setSortable(true);
        grid.addColumn(w -> w.activeTasks()).setHeader("Active Tasks").setSortable(true);
        grid.addColumn(w -> w.completedTasks()).setHeader("Completed Tasks").setSortable(true);

        grid.setItemDetailsRenderer(new com.vaadin.flow.data.renderer.ComponentRenderer<>(workload -> {
            VerticalLayout details = new VerticalLayout();
            details.setPadding(true);
            details.setSpacing(false);
            details.getStyle().set("gap", "var(--vaadin-space-xs)");

            if (workload.tasks().isEmpty()) {
                details.add(new Span("No tasks assigned"));
                return details;
            }

            // Group tasks by project
            Map<String, java.util.List<Task>> byProject = workload.tasks().stream()
                    .collect(Collectors.groupingBy(t -> t.getProject().getName()));

            for (var entry : byProject.entrySet()) {
                Span projectHeader = new Span(entry.getKey());
                projectHeader.getStyle()
                        .set("font-weight", "600")
                        .set("margin-top", "var(--vaadin-space-s)");
                details.add(projectHeader);

                for (Task task : entry.getValue()) {
                    Span taskRow = new Span(
                            task.getName() + " — " +
                            task.getStartDate() + " to " + task.getEndDate() + " — " +
                            task.getStatus().name().replace('_', ' '));
                    taskRow.getStyle()
                            .set("font-size", "var(--aura-font-size-s)")
                            .set("padding-left", "var(--vaadin-space-m)")
                            .set("cursor", "pointer")
                            .set("color", getStatusColor(task.getStatus()));
                    taskRow.getElement().addEventListener("click", e ->
                            UI.getCurrent().navigate("projects/" + task.getProject().getId() + "/tasks"));
                    details.add(taskRow);
                }
            }

            return details;
        }));

        // Highlight overloaded rows
        grid.setPartNameGenerator(workload -> workload.isOverloaded() ? "workload-warning-row" : null);

        grid.setItems(workloadService.getWorkloads());
        add(grid);
    }

    private String getStatusColor(TaskStatus status) {
        return switch (status) {
            case TODO -> "#616161";
            case IN_PROGRESS -> "#E65100";
            case DONE -> "#2E7D32";
        };
    }
}
