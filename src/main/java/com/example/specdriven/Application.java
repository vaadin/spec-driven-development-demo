package com.example.specdriven;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
public class Application implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.setPageTitle("CineMax");

        // Open Graph meta tags
        settings.addMetaTag("og:title", "CineMax");
        settings.addMetaTag("og:description", "Browse movies, pick your seats, and buy tickets at CineMax cinema");
        settings.addMetaTag("og:image", "/og-image.png");
        settings.addMetaTag("og:type", "website");

        // Twitter Card meta tags
        settings.addMetaTag("twitter:card", "summary_large_image");
        settings.addMetaTag("twitter:title", "CineMax");
        settings.addMetaTag("twitter:description", "Browse movies, pick your seats, and buy tickets at CineMax cinema");
        settings.addMetaTag("twitter:image", "/og-image.png");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
