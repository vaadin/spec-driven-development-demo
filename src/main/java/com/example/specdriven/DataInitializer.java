package com.example.specdriven;

import com.example.specdriven.show.Show;
import com.example.specdriven.show.ShowRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ShowRepository showRepository;

    public DataInitializer(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    @Override
    public void run(String... args) {
        if (showRepository.count() > 0) {
            return;
        }

        showRepository.save(createShow(
                "Les Misérables",
                "The epic tale of broken dreams, passion, sacrifice, and redemption set against the backdrop of 19th-century France. One of the most celebrated musicals of all time.",
                "https://picsum.photos/seed/lesmis/400/300",
                LocalDateTime.of(2026, 2, 22, 18, 30),
                new BigDecimal("64.99"),
                0 // Sold out
        ));

        showRepository.save(createShow(
                "The Lion King",
                "Disney's beloved coming-of-age story brought to life on stage with stunning puppetry, breathtaking visuals, and an award-winning score.",
                "https://picsum.photos/seed/lionking/400/300",
                LocalDateTime.of(2026, 2, 28, 14, 0),
                new BigDecimal("74.99"),
                0 // Sold out
        ));

        showRepository.save(createShow(
                "Chicago",
                "Murder, greed, corruption, exploitation, adultery, and treachery — all those things we hold near and dear to our hearts. The longest-running American musical in Broadway history.",
                "https://picsum.photos/seed/chicago/400/300",
                LocalDateTime.of(2026, 3, 8, 20, 0),
                new BigDecimal("54.99"),
                200
        ));

        showRepository.save(createShow(
                "The Phantom of the Opera",
                "A mysterious phantom haunts the Paris Opera House in this legendary musical. Experience the drama, romance, and unforgettable music that has captivated audiences worldwide for decades.",
                "https://picsum.photos/seed/phantom/400/300",
                LocalDateTime.of(2026, 3, 15, 19, 30),
                new BigDecimal("59.99"),
                120
        ));

        showRepository.save(createShow(
                "Hamilton",
                "The story of America's founding father Alexander Hamilton, told through hip-hop, jazz, R&B, and Broadway styles. A revolutionary musical about revolution.",
                "https://picsum.photos/seed/hamilton/400/300",
                LocalDateTime.of(2026, 3, 20, 20, 0),
                new BigDecimal("89.99"),
                85
        ));

        showRepository.save(createShow(
                "Wicked",
                "The untold story of the witches of Oz. Before Dorothy arrived, two young women met and forged an unlikely friendship that would change the Land of Oz forever.",
                "https://picsum.photos/seed/wicked/400/300",
                LocalDateTime.of(2026, 4, 5, 19, 0),
                new BigDecimal("69.99"),
                3 // Few seats left
        ));
    }

    private Show createShow(String title, String description, String coverImageUrl,
                            LocalDateTime dateTime, BigDecimal price, int availableSeats) {
        Show show = new Show();
        show.setTitle(title);
        show.setDescription(description);
        show.setCoverImageUrl(coverImageUrl);
        show.setDateTime(dateTime);
        show.setPrice(price);
        show.setAvailableSeats(availableSeats);
        return show;
    }
}
