package com.example.specdriven.poster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getPoster(@PathVariable String fileName) throws IOException {
        Path file = POSTERS_DIR.resolve(fileName);
        if (!file.normalize().startsWith(POSTERS_DIR) || !Files.exists(file)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        byte[] data = Files.readAllBytes(file);
        String contentType = fileName.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";

        // ETag based on file modification time and size
        long lastModified = Files.getLastModifiedTime(file).toMillis();
        long size = Files.size(file);
        String etag = "\"" + lastModified + "-" + size + "\"";

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .eTag(etag)
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }
}
