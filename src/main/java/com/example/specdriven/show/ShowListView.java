package com.example.specdriven.show;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route("")
@PageTitle("QuickTicket — Browse Shows")
public class ShowListView extends VerticalLayout {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("EEE, MMM d yyyy 'at' h:mm a");

    public ShowListView(ShowService showService) {
        addClassName("show-list-view");
        setSizeFull();
        setPadding(true);
        setSpacing(false);

        H1 header = new H1("QuickTicket");
        header.addClassName("app-header");

        Paragraph subtitle = new Paragraph("Find your next show and get tickets in seconds.");
        subtitle.addClassName("app-subtitle");

        Div grid = new Div();
        grid.addClassName("show-grid");

        List<Show> shows = showService.findAllSorted();
        for (Show show : shows) {
            grid.add(createShowCard(show));
        }

        add(header, subtitle, grid);
    }

    private Div createShowCard(Show show) {
        Div card = new Div();
        card.addClassName("show-card");

        Image image = new Image(show.getCoverImageUrl(), show.getTitle());
        image.addClassName("show-card-image");

        Div content = new Div();
        content.addClassName("show-card-content");

        H3 title = new H3(show.getTitle());
        title.addClassName("show-card-title");

        Span dateTime = new Span(show.getDateTime().format(DATE_FMT));
        dateTime.addClassName("show-card-date");

        Span price = new Span(String.format("$%.2f", show.getPrice()));
        price.addClassName("show-card-price");

        content.add(title, dateTime, price);

        if (show.isSoldOut()) {
            card.addClassName("sold-out");
            Span badge = new Span("SOLD OUT");
            badge.addClassName("sold-out-badge");
            content.add(badge);
        } else {
            card.getStyle().set("cursor", "pointer");
            card.addClickListener(e ->
                    card.getUI().ifPresent(ui -> ui.navigate("show/" + show.getId())));
            if (show.getAvailableSeats() <= 5) {
                Span lowStock = new Span("Only " + show.getAvailableSeats() + " left!");
                lowStock.addClassName("low-stock-badge");
                content.add(lowStock);
            }
        }

        card.add(image, content);
        return card;
    }
}
