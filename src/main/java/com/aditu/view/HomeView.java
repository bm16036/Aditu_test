package com.aditu.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

@Route(value = "home", layout = MainView.class)
public class HomeView extends VerticalLayout {

    public HomeView() {

        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
        setSpacing(true);
        setPadding(true);
        getStyle().set("margin-top", "50px");

        H2 projectTitle = new H2("Bienvenido a Aditu");
        projectTitle.getStyle()
                .set("font-size", "32px")
                .set("color", "#0D47A1")
                .set("margin-bottom", "10px");

        Paragraph description = new Paragraph("Aditu es una plataforma intuitiva y moderna para que los tutores gestionen clases, reuniones y eventos académicos con eficiencia.");
        description.getStyle()
                .set("font-size", "18px")
                .set("max-width", "600px")
                .set("text-align", "center")
                .set("color", "#555");

        Image centralImage = new Image("images/logo.png", "Imagen decorativa");
        centralImage.setWidth("400px");

        add(projectTitle, description, centralImage);
    }
}
