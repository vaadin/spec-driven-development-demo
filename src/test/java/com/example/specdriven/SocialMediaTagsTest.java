package com.example.specdriven;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UC-006: Social Media Tags & OG Image.
 * Verifies that OG and Twitter Card meta tags are present in the index.html
 * and the OG image asset exists.
 */
class SocialMediaTagsTest {

    private static String indexHtml;

    @BeforeAll
    static void loadIndexHtml() throws IOException {
        Path indexPath = Path.of("src/main/frontend/index.html");
        assertTrue(Files.exists(indexPath), "index.html must exist");
        indexHtml = Files.readString(indexPath);
    }

    // --- OG tags (BR-01, BR-05, BR-06) ---

    @Test
    void htmlContainsOgTitle() {
        assertTrue(indexHtml.contains("og:title"), "Missing og:title meta tag");
        assertTrue(indexHtml.contains("\"CineMax\""), "og:title should be CineMax");
    }

    @Test
    void htmlContainsOgDescription() {
        assertTrue(indexHtml.contains("og:description"), "Missing og:description meta tag");
        assertTrue(indexHtml.contains("Browse movies, pick your seats, and buy tickets at CineMax cinema"),
                "og:description content mismatch");
    }

    @Test
    void htmlContainsOgImage() {
        assertTrue(indexHtml.contains("og:image"), "Missing og:image meta tag");
        assertTrue(indexHtml.contains("og-image.png"), "og:image should reference og-image.png");
    }

    @Test
    void htmlContainsOgType() {
        assertTrue(indexHtml.contains("og:type"), "Missing og:type meta tag");
        assertTrue(indexHtml.contains("\"website\""), "og:type should be website");
    }

    // --- Twitter Card tags (BR-02) ---

    @Test
    void htmlContainsTwitterCard() {
        assertTrue(indexHtml.contains("twitter:card"), "Missing twitter:card meta tag");
        assertTrue(indexHtml.contains("summary_large_image"), "twitter:card should be summary_large_image");
    }

    @Test
    void htmlContainsTwitterTitle() {
        assertTrue(indexHtml.contains("twitter:title"), "Missing twitter:title meta tag");
    }

    @Test
    void htmlContainsTwitterDescription() {
        assertTrue(indexHtml.contains("twitter:description"), "Missing twitter:description meta tag");
    }

    @Test
    void htmlContainsTwitterImage() {
        assertTrue(indexHtml.contains("twitter:image"), "Missing twitter:image meta tag");
        assertTrue(indexHtml.contains("og-image.png"), "twitter:image should reference og-image.png");
    }

    // --- OG tags use property attribute, not name (BR-01) ---

    @Test
    void ogTagsUsePropertyAttribute() {
        assertTrue(indexHtml.contains("property=\"og:title\""), "og:title should use property attribute");
        assertTrue(indexHtml.contains("property=\"og:description\""), "og:description should use property attribute");
        assertTrue(indexHtml.contains("property=\"og:image\""), "og:image should use property attribute");
        assertTrue(indexHtml.contains("property=\"og:type\""), "og:type should use property attribute");
    }

    // --- OG image asset exists (BR-03, BR-04) ---

    @Test
    void ogImageFileExists() {
        Path ogImagePath = Path.of("src/main/resources/META-INF/resources/og-image.png");
        assertTrue(Files.exists(ogImagePath), "og-image.png must exist as a static asset");
    }

    @Test
    void ogImageFileIsAtLeast1200x630() throws IOException {
        Path ogImagePath = Path.of("src/main/resources/META-INF/resources/og-image.png");
        byte[] imageBytes = Files.readAllBytes(ogImagePath);

        // PNG header: width at bytes 16-19, height at bytes 20-23 (big-endian)
        assertTrue(imageBytes.length > 24, "File too small to be a valid PNG");
        assertEquals((byte) 0x89, imageBytes[0], "Not a valid PNG file");

        int width = ((imageBytes[16] & 0xFF) << 24) | ((imageBytes[17] & 0xFF) << 16)
                | ((imageBytes[18] & 0xFF) << 8) | (imageBytes[19] & 0xFF);
        int height = ((imageBytes[20] & 0xFF) << 24) | ((imageBytes[21] & 0xFF) << 16)
                | ((imageBytes[22] & 0xFF) << 8) | (imageBytes[23] & 0xFF);

        assertTrue(width >= 1200, "OG image width should be at least 1200px, was " + width);
        assertTrue(height >= 630, "OG image height should be at least 630px, was " + height);
    }
}
