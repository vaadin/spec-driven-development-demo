package com.example.specdriven.poster;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.ClassPathResource;
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
        // Reject path traversal
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String contentType = fileName.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
        byte[] data;

        // Try filesystem first (for uploaded posters in dev)
        Path fsFile = POSTERS_DIR.resolve(fileName);
        if (Files.exists(fsFile)) {
            data = Files.readAllBytes(fsFile);
            long lastModified = Files.getLastModifiedTime(fsFile).toMillis();
            String etag = "\"" + lastModified + "-" + data.length + "\"";
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .eTag(etag)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(data);
        }

        // Fall back to classpath (for bundled posters in production)
        var classpathResource = new ClassPathResource("posters/" + fileName);
        if (classpathResource.exists()) {
            try (InputStream is = classpathResource.getInputStream()) {
                data = is.readAllBytes();
            }
            String etag = "\"cp-" + data.length + "\"";
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .eTag(etag)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(data);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
