package com.example.specdriven.security;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false)
@PageTitle("Login — Triage")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login;
    private Div hintContainer;

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background-color", "#F7F9FB");

        login = new LoginForm();
        login.setAction("login");
        login.setForgotPasswordButtonVisible(true);
        login.addForgotPasswordListener(e -> showCredentialHint());

        hintContainer = new Div();
        hintContainer.setVisible(false);
        hintContainer.getStyle()
                .set("background-color", "#EBF3F8")
                .set("padding", "var(--vaadin-padding-m)")
                .set("border-radius", "var(--vaadin-radius-m)")
                .set("color", "#2C3E50")
                .set("max-width", "320px")
                .set("text-align", "center");

        Paragraph hint = new Paragraph("Demo credentials: username admin, password admin");
        hintContainer.add(hint);

        add(login, hintContainer);
    }

    private void showCredentialHint() {
        hintContainer.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}
