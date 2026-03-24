package com.example.specdriven.security;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
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
@PageTitle("Login — Stash.log")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm;
    private final Div credentialsHint;

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Image logo = new Image("images/logo.svg", "Stash.log");
        logo.setWidth("220px");
        logo.getStyle().set("margin-bottom", "var(--vaadin-gap-m)");

        loginForm = new LoginForm();
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(true);

        credentialsHint = new Div();
        credentialsHint.setVisible(false);
        credentialsHint.addClassName("credentials-hint");

        H3 hintTitle = new H3("Demo Credentials");
        hintTitle.getStyle().set("margin", "0 0 var(--vaadin-gap-xs) 0")
                .set("font-size", "var(--aura-font-size-m)");
        Paragraph adminHint = new Paragraph("Admin: admin / admin");
        adminHint.getStyle().set("margin", "0");
        Paragraph staffHint = new Paragraph("Staff: staff / staff");
        staffHint.getStyle().set("margin", "0");
        credentialsHint.add(hintTitle, adminHint, staffHint);

        loginForm.addForgotPasswordListener(e -> credentialsHint.setVisible(true));

        add(logo, loginForm, credentialsHint);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
