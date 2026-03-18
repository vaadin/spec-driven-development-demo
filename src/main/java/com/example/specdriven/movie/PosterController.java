package com.example.specdriven.movie;

import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posters")
public class PosterController {

    private static final Path POSTERS_DIR = Path.of("posters");

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getPoster(@PathVariable String filename) {
        Path file = POSTERS_DIR.resolve(filename).normalize();
        if (!file.startsWith(POSTERS_DIR) || !Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = filename.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new FileSystemResource(file));
    }
}
