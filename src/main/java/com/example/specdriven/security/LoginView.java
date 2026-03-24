package com.example.specdriven.security;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false)
@PageTitle("Login — GreenLedger")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm;
    private Div credentialsHint;

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H1 title = new H1("GreenLedger");
        title.getStyle().set("color", "var(--primary)");

        Paragraph tagline = new Paragraph("Expenses, simplified.");
        tagline.getStyle().set("color", "var(--text-secondary)");

        loginForm = new LoginForm();
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(true);
        loginForm.addForgotPasswordListener(e -> showCredentialsHint());

        credentialsHint = new Div();
        credentialsHint.setVisible(false);
        credentialsHint.getStyle()
                .set("background", "var(--surface)")
                .set("padding", "var(--vaadin-space-m)")
                .set("border-radius", "var(--vaadin-radius-m)")
                .set("margin-top", "var(--vaadin-space-m)")
                .set("max-width", "320px")
                .set("width", "100%");

        Paragraph hintTitle = new Paragraph("Demo Accounts:");
        hintTitle.getStyle().set("font-weight", "bold").set("margin", "0 0 var(--vaadin-space-s) 0");

        Div employeeHint = new Div(new Span("Employee: "), createCode("employee"), new Span(" / "), createCode("employee"));
        Div managerHint = new Div(new Span("Manager: "), createCode("manager"), new Span(" / "), createCode("manager"));

        credentialsHint.add(hintTitle, employeeHint, managerHint);

        add(title, tagline, loginForm, credentialsHint);
    }

    private Span createCode(String text) {
        Span code = new Span(text);
        code.getStyle()
                .set("font-family", "monospace")
                .set("background", "var(--vaadin-input-field-background)")
                .set("padding", "2px 6px")
                .set("border-radius", "var(--vaadin-radius-s)");
        return code;
    }

    private void showCredentialsHint() {
        credentialsHint.setVisible(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
