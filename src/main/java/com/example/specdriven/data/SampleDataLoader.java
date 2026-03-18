package com.example.specdriven.data;

import com.example.specdriven.movie.Movie;
import com.example.specdriven.movie.MovieRepository;
import com.example.specdriven.show.ScreeningRoom;
import com.example.specdriven.show.ScreeningRoomRepository;
import com.example.specdriven.show.Show;
import com.example.specdriven.show.ShowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ScreeningRoomRepository screeningRoomRepository;
    private final ShowRepository showRepository;

    public SampleDataLoader(MovieRepository movieRepository,
                            ScreeningRoomRepository screeningRoomRepository,
                            ShowRepository showRepository) {
        this.movieRepository = movieRepository;
        this.screeningRoomRepository = screeningRoomRepository;
        this.showRepository = showRepository;
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return;
        }

        List<Movie> movies = movieRepository.saveAll(List.of(
            new Movie("AI Developer 2", "The sequel to the hit AI movie. A developer's AI assistant becomes sentient and starts writing its own specs.", 128, "ai-developer-2.png"),
            new Movie("The Gardening Incident", "A peaceful afternoon in the garden turns into a thriller when the roses fight back.", 95, "gardening-incident.png"),
            new Movie("Living in the Forest", "A documentary about people who chose to leave civilization and live among the trees.", 110, "living-in-the-forest.png"),
            new Movie("The Null Mistake", "A Java developer accidentally deploys a null check that crashes the world's banking system.", 102, "null-mistake-java.png"),
            new Movie("Pink Elephants", "A surreal animated journey through a world where elephants are pink and gravity is optional.", 88, "pink-elephants.png"),
            new Movie("Reindeer Hunter", "A wildlife photographer's quest to capture the perfect shot of the elusive arctic reindeer.", 115, "reindeer-hunter.png"),
            new Movie("Sleeping with the Fishes", "An undercover agent infiltrates a marine biology lab with a dark secret.", 120, "sleeping-with-the-fishes.png"),
            new Movie("Threading a Needle", "A heist film where the team must thread through impossible security systems, one lock at a time.", 135, "threading-a-needle.png")
        ));

        List<ScreeningRoom> rooms = screeningRoomRepository.saveAll(List.of(
            new ScreeningRoom("Room 1", 8, 10),
            new ScreeningRoom("Room 2", 6, 8),
            new ScreeningRoom("Room 3", 5, 6)
        ));

        LocalTime[] showTimes = {
            LocalTime.of(10, 0), LocalTime.of(13, 30),
            LocalTime.of(16, 0), LocalTime.of(19, 0), LocalTime.of(21, 30)
        };

        int movieIndex = 0;
        for (int day = 0; day < 7; day++) {
            LocalDate date = LocalDate.now().plusDays(day);
            for (ScreeningRoom room : rooms) {
                for (int s = 0; s < 3; s++) {
                    LocalDateTime dateTime = LocalDateTime.of(date, showTimes[(day + s) % showTimes.length]);
                    Movie movie = movies.get(movieIndex % movies.size());
                    showRepository.save(new Show(dateTime, movie, room));
                    movieIndex++;
                }
            }
        }
    }
}
