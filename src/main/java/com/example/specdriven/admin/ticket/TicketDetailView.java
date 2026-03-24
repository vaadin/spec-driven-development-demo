package com.example.specdriven.admin.ticket;

import com.example.specdriven.model.Comment;
import com.example.specdriven.model.Status;
import com.example.specdriven.model.Ticket;
import com.example.specdriven.service.TicketService;
import com.example.specdriven.admin.AdminLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "admin/ticket/:ticketId", layout = AdminLayout.class)
@PageTitle("Ticket Detail | re:solve")
@RolesAllowed("ADMIN")
public class TicketDetailView extends VerticalLayout implements BeforeEnterObserver {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TicketService ticketService;
    private Ticket ticket;

    private final VerticalLayout detailSection = new VerticalLayout();
    private final VerticalLayout commentsSection = new VerticalLayout();
    private final TextArea commentInput = new TextArea();
    private Select<Status> statusSelect;

    public TicketDetailView(TicketService ticketService) {
        this.ticketService = ticketService;
        setPadding(true);
        setSpacing(true);
        getStyle().set("max-width", "1200px").set("margin", "0 auto");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String ticketIdParam = event.getRouteParameters().get("ticketId").orElse("");
        try {
            Long ticketId = Long.parseLong(ticketIdParam);
            ticket = ticketService.getTicketDetail(ticketId);
            buildView();
        } catch (Exception e) {
            event.forwardTo("admin/queue");
        }
    }

    private void buildView() {
        removeAll();

        RouterLink backLink = new RouterLink("← Back to Queue", com.example.specdriven.admin.queue.TicketQueueView.class);
        backLink.getStyle().set("font-size", "var(--aura-font-size-s)").set("color", "var(--vaadin-primary-color)");
        add(backLink);

        // Ticket header
        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(Alignment.CENTER);
        header.setWidthFull();

        H1 title = new H1("#" + ticket.getId() + " " + ticket.getTitle());
        title.getStyle().set("font-size", "var(--aura-font-size-xl)").set("margin", "0");

        Span statusBadge = createStatusBadge(ticket.getStatus());

        header.add(title, statusBadge);
        header.expand(title);
        add(header);

        // Ticket metadata
        buildDetailSection();
        add(detailSection);

        // Status change
        if (ticket.getStatus() != Status.CLOSED) {
            add(buildStatusChangeSection());
        }

        // Description
        Div descDiv = new Div();
        descDiv.getStyle()
                .set("background", "white")
                .set("border", "1px solid #e2e8f0")
                .set("border-radius", "var(--vaadin-radius-m)")
                .set("padding", "var(--vaadin-padding-m)");
        H2 descTitle = new H2("Description");
        descTitle.getStyle().set("font-size", "var(--aura-font-size-m)").set("margin", "0 0 8px 0");
        Paragraph descText = new Paragraph(ticket.getDescription());
        descText.getStyle().set("white-space", "pre-wrap").set("color", "#475569");
        descDiv.add(descTitle, descText);
        add(descDiv);

        // Comments
        buildCommentsSection();
        add(commentsSection);

        // Comment form
        if (ticket.getStatus() != Status.CLOSED) {
            add(buildCommentForm());
        }
    }

    private void buildDetailSection() {
        detailSection.removeAll();
        detailSection.setPadding(true);
        detailSection.getStyle()
                .set("background", "#f8fafc")
                .set("border-radius", "var(--vaadin-radius-m)")
                .set("gap", "var(--vaadin-gap-s)");

        HorizontalLayout metaRow = new HorizontalLayout();
        metaRow.setWidthFull();
        metaRow.getStyle().set("flex-wrap", "wrap").set("gap", "var(--vaadin-gap-l)");

        metaRow.add(createMetaItem("Category", ticket.getCategory().name()));
        metaRow.add(createMetaItem("Priority", ticket.getPriority().name()));
        metaRow.add(createMetaItem("Created by", ticket.getCreatedBy() != null ? ticket.getCreatedBy().getName() : ""));
        metaRow.add(createMetaItem("Assigned to", ticket.getAssignedTo() != null ? ticket.getAssignedTo().getName() : "Unassigned"));
        metaRow.add(createMetaItem("Created", ticket.getCreatedDate() != null ? ticket.getCreatedDate().format(FORMATTER) : ""));
        metaRow.add(createMetaItem("Updated", ticket.getUpdatedDate() != null ? ticket.getUpdatedDate().format(FORMATTER) : ""));

        detailSection.add(metaRow);
    }

    private Div createMetaItem(String label, String value) {
        Div item = new Div();
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "var(--aura-font-size-xs)")
                .set("font-weight", "var(--aura-font-weight-medium)")
                .set("color", "#94a3b8")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.05em")
                .set("display", "block");
        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("display", "block");
        item.add(labelSpan, valueSpan);
        return item;
    }

    private HorizontalLayout buildStatusChangeSection() {
        List<Status> validStatuses = ticketService.getValidNextStatuses(ticket.getStatus());

        statusSelect = new Select<>();
        statusSelect.setLabel("Change Status");
        statusSelect.setItems(validStatuses);
        statusSelect.setPlaceholder("Select new status");

        Button changeBtn = new Button("Update Status", e -> {
            Status newStatus = statusSelect.getValue();
            if (newStatus != null) {
                ticketService.changeStatus(ticket.getId(), newStatus);
                Notification.show("Status updated to " + newStatus.name(), 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                // Reload
                ticket = ticketService.getTicketDetail(ticket.getId());
                buildView();
            }
        });
        changeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout layout = new HorizontalLayout(statusSelect, changeBtn);
        layout.setAlignItems(Alignment.BASELINE);
        return layout;
    }

    private void buildCommentsSection() {
        commentsSection.removeAll();
        commentsSection.setPadding(false);

        H2 commentsTitle = new H2("Comments (" + ticket.getComments().size() + ")");
        commentsTitle.getStyle().set("font-size", "var(--aura-font-size-m)").set("margin", "0");
        commentsSection.add(commentsTitle);

        if (ticket.getComments().isEmpty()) {
            Paragraph noComments = new Paragraph("No comments yet.");
            noComments.getStyle().set("color", "#94a3b8").set("font-style", "italic");
            commentsSection.add(noComments);
        } else {
            for (Comment comment : ticket.getComments()) {
                commentsSection.add(createCommentCard(comment));
            }
        }
    }

    private Div createCommentCard(Comment comment) {
        Div card = new Div();
        card.getStyle()
                .set("padding", "var(--vaadin-padding-s) var(--vaadin-padding-m)")
                .set("background", "#f8fafc")
                .set("border-radius", "var(--vaadin-radius-m)")
                .set("border", "1px solid #f1f5f9");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span author = new Span(comment.getAuthor().getName());
        author.getStyle().set("font-weight", "var(--aura-font-weight-medium)").set("font-size", "var(--aura-font-size-s)");

        Span date = new Span(comment.getCreatedDate() != null ? comment.getCreatedDate().format(FORMATTER) : "");
        date.getStyle().set("font-size", "var(--aura-font-size-s)").set("color", "#94a3b8");

        header.add(author, date);

        Paragraph text = new Paragraph(comment.getText());
        text.getStyle().set("margin", "4px 0 0 0").set("font-size", "var(--aura-font-size-s)").set("white-space", "pre-wrap");

        card.add(header, text);
        return card;
    }

    private HorizontalLayout buildCommentForm() {
        commentInput.setPlaceholder("Add a comment...");
        commentInput.setWidthFull();
        commentInput.setMinHeight("80px");

        Button addBtn = new Button("Add Comment", e -> {
            String text = commentInput.getValue();
            if (text == null || text.trim().isEmpty()) {
                Notification.show("Comment cannot be empty", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            ticketService.addComment(ticket.getId(), text.trim());
            commentInput.clear();
            Notification.show("Comment added", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            // Reload
            ticket = ticketService.getTicketDetail(ticket.getId());
            buildView();
        });
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout form = new HorizontalLayout(commentInput, addBtn);
        form.setWidthFull();
        form.setAlignItems(Alignment.END);
        form.expand(commentInput);
        return form;
    }

    private Span createStatusBadge(Status status) {
        Span badge = new Span(status.name().replace("_", " "));
        badge.addClassName("status-badge");
        badge.addClassName("status-" + status.name().toLowerCase().replace("_", "-"));
        return badge;
    }
}
