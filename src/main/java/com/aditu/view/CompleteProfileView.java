package com.aditu.view;

import com.aditu.model.Tutor;
import com.aditu.service.TutorService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "complete-profile", layout = MainView.class)
public class CompleteProfileView extends VerticalLayout {

    public CompleteProfileView(TutorService tutorService) {
        H2 title = new H2("Completa tu perfil");

        TextField nombresField = new TextField("Nombres");
        TextField apellidosField = new TextField("Apellidos");
        TextField usernameField = new TextField("Nombre de Usuario");

        // Cargar datos actuales del tutor
        try {
            Tutor currentTutor = tutorService.getCurrentTutor();
            nombresField.setValue(currentTutor.getNombres() != null ? currentTutor.getNombres() : "");
            apellidosField.setValue(currentTutor.getApellidos() != null ? currentTutor.getApellidos() : "");
            usernameField.setValue(currentTutor.getUsername() != null ? currentTutor.getUsername() : "");
        } catch (IllegalStateException e) {
            Notification.show("Error al cargar los datos del tutor: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("login"); // Redirigir al login si no hay un tutor autenticado
        }

        Button saveButton = new Button("Guardar", event -> {
            String nombres = nombresField.getValue();
            String apellidos = apellidosField.getValue();
            String username = usernameField.getValue();

            try {
                tutorService.updateProfile(nombres, apellidos, username);
                Notification.show("Perfil actualizado con éxito.");
                getUI().ifPresent(ui -> ui.navigate("home"));
            } catch (IllegalArgumentException e) {
                Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        add(title, nombresField, apellidosField, usernameField, saveButton);
        setAlignItems(Alignment.CENTER);
    }
}