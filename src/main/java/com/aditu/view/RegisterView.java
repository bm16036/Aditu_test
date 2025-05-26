package com.aditu.view;

import com.aditu.model.Tutor;
import com.aditu.service.TutorService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;

@Route(value = "register", layout = MainView.class)
public class RegisterView extends VerticalLayout {

    public RegisterView(TutorService tutorService) {
        // Título
        H2 title = new H2("Registro de Tutor");
        title.getStyle()
                .set("color", "#0D47A1")
                .set("font-size", "28px")
                .set("margin-bottom", "20px");

        // Campos de entrada
        EmailField emailField = new EmailField("Correo Electrónico");
        emailField.setPlaceholder("Ejemplo: correo@dominio.com");
        emailField.setWidth("300px");

        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.setPlaceholder("Ingresa tu contraseña");
        passwordField.setWidth("300px");

        PasswordField confirmPasswordField = new PasswordField("Confirmar Contraseña");
        confirmPasswordField.setPlaceholder("Repite tu contraseña");
        confirmPasswordField.setWidth("300px");

        // Botón de registro
        Button registerButton = new Button("Registrarse");
        registerButton.getStyle()
                .set("background-color", "#0D47A1")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 20px");
        registerButton.addClickListener(event -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (!password.equals(confirmPassword)) {
                Notification.show("Las contraseñas no coinciden", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                Tutor tutor = new Tutor();
                tutor.setCorreoElectronico(email);
                tutor.setPassword(password);
                tutorService.registerTutor(tutor);
                Notification.show("Registro exitoso. Ahora puedes iniciar sesión.");
                getUI().ifPresent(ui -> ui.navigate("complete-"));
            } catch (IllegalArgumentException e) {
                Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        // Contenedor para centrar los elementos
        FlexLayout container = new FlexLayout();
        container.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        container.setAlignItems(Alignment.CENTER);
        container.setJustifyContentMode(JustifyContentMode.CENTER);
        container.setWidthFull();
        container.add(title, emailField, passwordField, confirmPasswordField, registerButton);

        // Estilo general
        getStyle().set("background-color", "#F5F5F5");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(container);
    }
}