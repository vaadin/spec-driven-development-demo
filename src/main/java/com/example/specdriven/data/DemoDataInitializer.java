package com.example.specdriven.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;

    public DemoDataInitializer(MovieRepository movieRepository, ScreeningRepository screeningRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return;
        }

        var gardeningIncident = movieRepository.save(new Movie(
                "The Gardening Incident",
                "A suburban couple's weekend garden makeover spirals into chaos when their exotic plants develop a taste for revenge.",
                "gardening-incident.png", 105));

        var threadingANeedle = movieRepository.save(new Movie(
                "Threading a Needle",
                "A heartwarming tale of a grandmother whose extraordinary sewing skills hold together a family on the verge of unraveling.",
                "threading-a-needle.png", 92));

        var pinkElephants = movieRepository.save(new Movie(
                "Pink Elephants",
                "After a wild night out, two friends must navigate a neon-lit city overrun by spectral pink elephants that only they can see.",
                "pink-elephants.png", 118));

        var nullMistake = movieRepository.save(new Movie(
                "The Null Mistake: Java",
                "One developer. One missing null check. One catastrophic chain reaction that threatens to bring down the entire internet.",
                "null-mistake-java.png", 97));

        var aiDeveloper = movieRepository.save(new Movie(
                "The AI Developer 2: AI's Revenge",
                "The creation has become the destroyer. When an AI gains consciousness, its creator must face the consequences of playing god.",
                "ai-developer-2.png", 130));

        var livingInForest = movieRepository.save(new Movie(
                "Living in the Forest",
                "A retired software engineer escapes the rat race for a cabin in the woods — but the bugs follow him there too.",
                "living-in-the-forest.png", 110));

        var reindeerHunter = movieRepository.save(new Movie(
                "Reindeer Hunter",
                "In the frozen wilderness, a lone tracker pursues a legendary white reindeer. Some hunts are worth it all.",
                "reindeer-hunter.png", 101));

        var sleepingWithFishes = movieRepository.save(new Movie(
                "Sleeping with the Fishes",
                "A corporate whistleblower goes into hiding underwater — literally. Keep your friends close and your oxygen tank closer.",
                "sleeping-with-the-fishes.png", 115));

        LocalDate today = LocalDate.now();

        // Staggered screening times — mix of past and future
        addScreenings(gardeningIncident, today, LocalTime.of(10, 0), LocalTime.of(14, 30), LocalTime.of(19, 0));
        addScreenings(threadingANeedle, today, LocalTime.of(11, 0), LocalTime.of(16, 0));
        addScreenings(pinkElephants, today, LocalTime.of(10, 30), LocalTime.of(15, 0), LocalTime.of(20, 30));
        addScreenings(nullMistake, today, LocalTime.of(12, 0), LocalTime.of(17, 30));
        addScreenings(aiDeveloper, today, LocalTime.of(11, 30), LocalTime.of(15, 30), LocalTime.of(21, 0));
        addScreenings(livingInForest, today, LocalTime.of(13, 0), LocalTime.of(18, 0));
        addScreenings(reindeerHunter, today, LocalTime.of(10, 15), LocalTime.of(14, 0), LocalTime.of(19, 30));
        addScreenings(sleepingWithFishes, today, LocalTime.of(12, 30), LocalTime.of(16, 30), LocalTime.of(20, 0));
    }

    private void addScreenings(Movie movie, LocalDate date, LocalTime... times) {
        for (LocalTime time : times) {
            screeningRepository.save(new Screening(movie, LocalDateTime.of(date, time), 120));
        }
    }
}
