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
@PageTitle("Login | Forge")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();
    private Div credentialsHint;

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        login.setAction("login");
        login.setForgotPasswordButtonVisible(true);
        login.addForgotPasswordListener(e -> showCredentialsHint());

        credentialsHint = new Div();
        credentialsHint.addClassName("credentials-hint");
        credentialsHint.setVisible(false);

        Paragraph hintText = new Paragraph("Demo credentials:");
        Paragraph adminCreds = new Paragraph("Admin: admin / admin");
        Paragraph userCreds = new Paragraph("User: user / user");
        credentialsHint.add(hintText, adminCreds, userCreds);

        H1 title = new H1("Forge");
        title.getStyle().set("color", "var(--vaadin-primary-color)");

        Paragraph tagline = new Paragraph("Project Management");
        tagline.getStyle().set("color", "#757575");

        add(title, tagline, login, credentialsHint);
    }

    private void showCredentialsHint() {
        credentialsHint.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
