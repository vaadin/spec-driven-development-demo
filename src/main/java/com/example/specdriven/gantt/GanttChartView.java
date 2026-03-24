package com.example.specdriven.gantt;

import com.example.specdriven.member.MemberRepository;
import com.example.specdriven.project.Project;
import com.example.specdriven.project.ProjectService;
import com.example.specdriven.task.*;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Route("projects/:projectId/gantt")
@PageTitle("Gantt Chart | Forge")
@PermitAll
public class GanttChartView extends VerticalLayout implements BeforeEnterObserver {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final TaskDependencyRepository dependencyRepository;
    private final boolean isAdmin;

    private Long projectId;
    private Project project;
    private String zoomLevel = "week";
    private Div chartContainer;

    public GanttChartView(TaskService taskService, ProjectService projectService,
                           TaskDependencyRepository dependencyRepository,
                           MemberRepository memberRepository) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.dependencyRepository = dependencyRepository;
        this.isAdmin = hasAdminRole();

        setPadding(true);
        setSpacing(true);
        setSizeFull();
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

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H2 title = new H2(project.getName() + " — Gantt Chart");

        HorizontalLayout controls = new HorizontalLayout();
        controls.setAlignItems(FlexComponent.Alignment.CENTER);

        Button dayBtn = new Button("Day", e -> { zoomLevel = "day"; renderChart(); });
        Button weekBtn = new Button("Week", e -> { zoomLevel = "week"; renderChart(); });
        Button monthBtn = new Button("Month", e -> { zoomLevel = "month"; renderChart(); });
        weekBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button backBtn = new Button("Back to Tasks",
                e -> UI.getCurrent().navigate("projects/" + projectId + "/tasks"));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        controls.add(dayBtn, weekBtn, monthBtn, backBtn);
        header.add(title, controls);
        add(header);

        // Chart container
        chartContainer = new Div();
        chartContainer.setWidthFull();
        chartContainer.getStyle()
                .set("flex", "1")
                .set("overflow", "auto")
                .set("position", "relative")
                .set("border", "1px solid #E0E0E0")
                .set("border-radius", "8px")
                .set("background", "white");
        add(chartContainer);

        renderChart();
    }

    private void renderChart() {
        chartContainer.removeAll();

        List<Task> tasks = taskService.findByProjectId(projectId);
        List<TaskDependency> dependencies = dependencyRepository.findByPredecessorTaskProjectId(projectId);

        if (tasks.isEmpty()) {
            chartContainer.add(new Span("No tasks to display. Add tasks to see the Gantt chart."));
            return;
        }

        LocalDate chartStart = project.getStartDate();
        LocalDate chartEnd = project.getEndDate();
        long totalDays = ChronoUnit.DAYS.between(chartStart, chartEnd) + 1;

        int dayWidth;
        switch (zoomLevel) {
            case "day" -> dayWidth = 40;
            case "month" -> dayWidth = 4;
            default -> dayWidth = 12; // week
        }

        int labelWidth = 200;
        int rowHeight = 40;
        int headerHeight = 30;
        int chartWidth = (int) (totalDays * dayWidth);

        // Main wrapper
        Div wrapper = new Div();
        wrapper.getStyle()
                .set("display", "flex")
                .set("width", (labelWidth + chartWidth) + "px")
                .set("min-height", ((tasks.size() * rowHeight) + headerHeight) + "px");

        // Left panel - task names
        Div leftPanel = new Div();
        leftPanel.getStyle()
                .set("width", labelWidth + "px")
                .set("min-width", labelWidth + "px")
                .set("border-right", "1px solid #E0E0E0");

        Div leftHeader = new Div();
        leftHeader.getStyle()
                .set("height", headerHeight + "px")
                .set("line-height", headerHeight + "px")
                .set("padding", "0 8px")
                .set("font-weight", "600")
                .set("font-size", "var(--aura-font-size-s)")
                .set("border-bottom", "1px solid #E0E0E0")
                .set("color", "#212121");
        leftHeader.setText("Task");
        leftPanel.add(leftHeader);

        for (Task task : tasks) {
            Div row = new Div();
            row.getStyle()
                    .set("height", rowHeight + "px")
                    .set("line-height", rowHeight + "px")
                    .set("padding", "0 8px")
                    .set("border-bottom", "1px solid #F5F5F5")
                    .set("font-size", "var(--aura-font-size-s)")
                    .set("overflow", "hidden")
                    .set("text-overflow", "ellipsis")
                    .set("white-space", "nowrap")
                    .set("color", "#212121");
            row.setText(task.getName());
            leftPanel.add(row);
        }

        // Right panel - timeline
        Div rightPanel = new Div();
        rightPanel.getStyle()
                .set("position", "relative")
                .set("flex", "1")
                .set("min-width", chartWidth + "px");

        // Timeline header with date labels
        Div timelineHeader = new Div();
        timelineHeader.getStyle()
                .set("height", headerHeight + "px")
                .set("position", "relative")
                .set("border-bottom", "1px solid #E0E0E0")
                .set("font-size", "11px")
                .set("color", "#757575");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d");
        int labelInterval = switch (zoomLevel) {
            case "day" -> 1;
            case "month" -> 7;
            default -> 7;
        };

        for (long d = 0; d < totalDays; d += labelInterval) {
            LocalDate date = chartStart.plusDays(d);
            Div label = new Div();
            label.setText(date.format(fmt));
            label.getStyle()
                    .set("position", "absolute")
                    .set("left", (d * dayWidth) + "px")
                    .set("top", "0")
                    .set("height", headerHeight + "px")
                    .set("line-height", headerHeight + "px")
                    .set("padding-left", "2px")
                    .set("border-left", "1px solid #E0E0E0");
            timelineHeader.add(label);
        }
        rightPanel.add(timelineHeader);

        // Task bars area
        Div barsArea = new Div();
        barsArea.getStyle()
                .set("position", "relative")
                .set("width", chartWidth + "px")
                .set("height", (tasks.size() * rowHeight) + "px");

        // Row backgrounds
        for (int i = 0; i < tasks.size(); i++) {
            Div rowBg = new Div();
            rowBg.getStyle()
                    .set("position", "absolute")
                    .set("left", "0")
                    .set("top", (i * rowHeight) + "px")
                    .set("width", "100%")
                    .set("height", rowHeight + "px")
                    .set("border-bottom", "1px solid #F5F5F5");
            barsArea.add(rowBg);
        }

        // Today marker
        LocalDate today = LocalDate.now();
        if (!today.isBefore(chartStart) && !today.isAfter(chartEnd)) {
            long todayOffset = ChronoUnit.DAYS.between(chartStart, today);
            Div todayLine = new Div();
            todayLine.getStyle()
                    .set("position", "absolute")
                    .set("left", (todayOffset * dayWidth) + "px")
                    .set("top", "0")
                    .set("width", "2px")
                    .set("height", "100%")
                    .set("background", "#E65100")
                    .set("z-index", "5");
            barsArea.add(todayLine);
        }

        // Task bars
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            long startOffset = ChronoUnit.DAYS.between(chartStart, task.getStartDate());
            long duration = ChronoUnit.DAYS.between(task.getStartDate(), task.getEndDate()) + 1;

            String barColor = switch (task.getStatus()) {
                case TODO -> "#BDBDBD";
                case IN_PROGRESS -> "#F57C00";
                case DONE -> "#2E7D32";
            };

            Div bar = new Div();
            bar.getStyle()
                    .set("position", "absolute")
                    .set("left", (startOffset * dayWidth) + "px")
                    .set("top", (i * rowHeight + 8) + "px")
                    .set("width", (duration * dayWidth - 2) + "px")
                    .set("height", (rowHeight - 16) + "px")
                    .set("background", barColor)
                    .set("border-radius", "4px")
                    .set("cursor", "pointer")
                    .set("z-index", "10")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("padding", "0 6px")
                    .set("overflow", "hidden");

            Span barLabel = new Span(task.getName());
            barLabel.getStyle()
                    .set("color", "white")
                    .set("font-size", "11px")
                    .set("font-weight", "500")
                    .set("white-space", "nowrap")
                    .set("overflow", "hidden")
                    .set("text-overflow", "ellipsis");
            bar.add(barLabel);

            bar.addClickListener(e ->
                    UI.getCurrent().navigate("projects/" + projectId + "/tasks"));

            barsArea.add(bar);
        }

        // Dependency arrows (simple line approach using SVG-like divs)
        for (TaskDependency dep : dependencies) {
            int predIdx = -1;
            int succIdx = -1;
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId().equals(dep.getPredecessorTask().getId())) predIdx = i;
                if (tasks.get(i).getId().equals(dep.getSuccessorTask().getId())) succIdx = i;
            }
            if (predIdx >= 0 && succIdx >= 0) {
                Task pred = tasks.get(predIdx);
                Task succ = tasks.get(succIdx);

                long predEndOffset = ChronoUnit.DAYS.between(chartStart, pred.getEndDate()) + 1;
                long succStartOffset = ChronoUnit.DAYS.between(chartStart, succ.getStartDate());

                int x1 = (int) (predEndOffset * dayWidth);
                int y1 = predIdx * rowHeight + rowHeight / 2;
                int x2 = (int) (succStartOffset * dayWidth);
                int y2 = succIdx * rowHeight + rowHeight / 2;

                // Horizontal line from pred end
                Div hLine = new Div();
                int hLeft = Math.min(x1, x2);
                int hWidth = Math.abs(x2 - x1);
                hLine.getStyle()
                        .set("position", "absolute")
                        .set("left", hLeft + "px")
                        .set("top", y1 + "px")
                        .set("width", Math.max(hWidth, 1) + "px")
                        .set("height", "2px")
                        .set("background", "#757575")
                        .set("z-index", "3");
                barsArea.add(hLine);

                // Vertical line
                if (y1 != y2) {
                    int vTop = Math.min(y1, y2);
                    int vHeight = Math.abs(y2 - y1);
                    Div vLine = new Div();
                    vLine.getStyle()
                            .set("position", "absolute")
                            .set("left", x2 + "px")
                            .set("top", vTop + "px")
                            .set("width", "2px")
                            .set("height", vHeight + "px")
                            .set("background", "#757575")
                            .set("z-index", "3");
                    barsArea.add(vLine);
                }

                // Arrow head
                Div arrow = new Div();
                arrow.getStyle()
                        .set("position", "absolute")
                        .set("left", (x2 - 4) + "px")
                        .set("top", (y2 - 4) + "px")
                        .set("width", "0")
                        .set("height", "0")
                        .set("border-top", "5px solid transparent")
                        .set("border-bottom", "5px solid transparent")
                        .set("border-left", "8px solid #757575")
                        .set("z-index", "4");
                barsArea.add(arrow);
            }
        }

        rightPanel.add(barsArea);

        wrapper.add(leftPanel, rightPanel);
        chartContainer.add(wrapper);
    }

    private boolean hasAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
