package com.example.specdriven.security;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false)
@PageTitle("Login | re:solve")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginOverlay loginOverlay = new LoginOverlay();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginOverlay.setTitle("re:solve");
        loginOverlay.setDescription("Help desk, simplified.");
        loginOverlay.setAction("login");
        loginOverlay.setOpened(true);

        loginOverlay.setForgotPasswordButtonVisible(true);
        loginOverlay.addForgotPasswordListener(event -> showDemoCredentials());

        add(loginOverlay);
    }

    private void showDemoCredentials() {
        Div credentialsDiv = new Div();
        credentialsDiv.getStyle()
                .set("padding", "var(--vaadin-padding-m)")
                .set("background", "var(--vaadin-background-container)")
                .set("border-radius", "var(--vaadin-radius-m)")
                .set("font-size", "var(--aura-font-size-s)");

        credentialsDiv.add(new Paragraph("Demo credentials:"));

        addCredentialRow(credentialsDiv, "Customer", "customer@test.com");
        addCredentialRow(credentialsDiv, "Agent", "agent@test.com");
        addCredentialRow(credentialsDiv, "Manager", "manager@test.com");

        Paragraph hint = new Paragraph("Password is the same as the email address.");
        hint.getStyle().set("font-style", "italic").set("margin-top", "var(--vaadin-gap-s)");
        credentialsDiv.add(hint);

        loginOverlay.getFooter().removeAll();
        loginOverlay.getFooter().add(credentialsDiv);
    }

    private void addCredentialRow(Div parent, String role, String email) {
        Div row = new Div();
        row.getStyle().set("margin", "var(--vaadin-gap-xs) 0");
        Span roleSpan = new Span(role + ": ");
        roleSpan.getStyle().set("font-weight", "var(--aura-font-weight-semibold)");
        Span emailSpan = new Span(email);
        row.add(roleSpan, emailSpan);
        parent.add(row);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginOverlay.setError(true);
        }
    }
}
