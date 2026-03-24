package com.example.specdriven.data;

import com.example.specdriven.movie.Movie;
import com.example.specdriven.movie.MovieRepository;
import com.example.specdriven.room.ScreeningRoom;
import com.example.specdriven.room.ScreeningRoomRepository;
import com.example.specdriven.show.Show;
import com.example.specdriven.show.ShowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ScreeningRoomRepository roomRepository;
    private final ShowRepository showRepository;

    public DataInitializer(MovieRepository movieRepository, ScreeningRoomRepository roomRepository,
            ShowRepository showRepository) {
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.showRepository = showRepository;
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            return; // Already seeded
        }

        // Movies
        List<Movie> movies = List.of(
                createMovie("AI Developer 2", "The sequel nobody asked for but everyone needed. A developer's AI assistant gains sentience and starts writing better code than its creator.", 128, "ai-developer-2.png"),
                createMovie("The Gardening Incident", "A peaceful afternoon of gardening turns into a thrilling adventure when mysterious plants start growing overnight.", 105, "gardening-incident.png"),
                createMovie("Living in the Forest", "A documentary following a family who decided to leave city life behind and build their dream home deep in the wilderness.", 92, "living-in-the-forest.png"),
                createMovie("The Null Mistake", "A Java programmer accidentally deploys code with an unchecked null reference, triggering a chain of events that threatens the global financial system.", 115, "null-mistake-java.png"),
                createMovie("Pink Elephants", "A whimsical animated adventure where a herd of pink elephants must save their enchanted valley from grayscale invaders.", 88, "pink-elephants.png"),
                createMovie("Reindeer Hunter", "A wildlife photographer ventures into the Arctic to capture the perfect reindeer migration shot, but nature has other plans.", 135, "reindeer-hunter.png"),
                createMovie("Sleeping with the Fishes", "An insomniac marine biologist discovers that sleeping underwater with fish is the only cure for their condition.", 98, "sleeping-with-the-fishes.png"),
                createMovie("Threading a Needle", "A masterful tailor must create the perfect suit in 24 hours to save their family business. A story of precision, passion, and perseverance.", 110, "threading-a-needle.png")
        );
        movies = movieRepository.saveAll(movies);

        // Screening Rooms
        ScreeningRoom room1 = createRoom("Room 1", 8, 10);
        ScreeningRoom room2 = createRoom("Room 2", 6, 8);
        ScreeningRoom room3 = createRoom("Room 3", 5, 6);
        List<ScreeningRoom> rooms = roomRepository.saveAll(List.of(room1, room2, room3));
        room1 = rooms.get(0);
        room2 = rooms.get(1);
        room3 = rooms.get(2);

        // Shows over the next 7 days
        LocalDate today = LocalDate.now();
        LocalTime[] times = {LocalTime.of(10, 0), LocalTime.of(13, 30), LocalTime.of(17, 0), LocalTime.of(20, 30)};

        int movieIndex = 0;
        for (int day = 0; day < 7; day++) {
            LocalDate date = today.plusDays(day + 1);
            ScreeningRoom[] dayRooms = {room1, room2, room3};

            for (ScreeningRoom room : dayRooms) {
                // 2 shows per room per day
                for (int t = 0; t < 2; t++) {
                    int timeIndex = (day + t * 2) % times.length;
                    Show show = new Show();
                    show.setMovie(movies.get(movieIndex % movies.size()));
                    show.setScreeningRoom(room);
                    show.setDateTime(LocalDateTime.of(date, times[timeIndex]));
                    showRepository.save(show);
                    movieIndex++;
                }
            }
        }
    }

    private Movie createMovie(String title, String description, int duration, String poster) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setDurationMinutes(duration);
        movie.setPosterFileName(poster);
        return movie;
    }

    private ScreeningRoom createRoom(String name, int rows, int seatsPerRow) {
        ScreeningRoom room = new ScreeningRoom();
        room.setName(name);
        room.setRows(rows);
        room.setSeatsPerRow(seatsPerRow);
        return room;
    }
}
