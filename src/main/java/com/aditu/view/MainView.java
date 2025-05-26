package com.aditu.view;

import com.aditu.service.TutorService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Route(value = "/")
public class MainView extends AppLayout {

    private final TutorService tutorService;

    public MainView(TutorService tutorService) {
        this.tutorService = tutorService;
        checkProfileCompletion();
        createHeader();
        createDrawer();
    }

    private void checkProfileCompletion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
        if (!isAuthenticated) {
            UI.getCurrent().navigate("login");
            return;
        }
        try {
            var currentTutor = tutorService.getCurrentTutor();
            if (currentTutor.getNombres() == null || currentTutor.getApellidos() == null || currentTutor.getUsername() == null) {
                UI.getCurrent().navigate("complete-profile");
            }
        } catch (Exception e) {
            // Si ocurre un error pero el usuario está autenticado, puedes mostrar un mensaje o redirigir a home
            UI.getCurrent().navigate("home");
        }
    }

    private void createHeader() {
        Image logoImage = new Image("images/logo.png", "Aditu Logo");
        logoImage.setHeight("60px");
        logoImage.getStyle().set("cursor", "pointer");
        logoImage.addClickListener(e -> UI.getCurrent().navigate(HomeView.class));

        H2 logoText = new H2("ADITU | Plataforma de Tutores");
        logoText.getStyle()
                .set("margin-left", "10px")
                .set("font-size", "28px")
                .set("color", "#004085")
                .set("margin", "0")
                .set("cursor", "pointer");
        logoText.addClickListener(e -> UI.getCurrent().navigate("/"));

        // Obtener usuario autenticado
        String userEmail = "";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof OidcUser oidcUser) {
            userEmail = oidcUser.getEmail();
        }

        // Menú desplegable para el usuario
        Button userButton = new Button(userEmail, VaadinIcon.USER.create());
        userButton.getStyle().set("margin-left", "auto").set("margin-right", "20px");

        ContextMenu userMenu = new ContextMenu(userButton);
        userMenu.setOpenOnClick(true);
        userMenu.addItem("Cerrar sesión", e -> UI.getCurrent().getPage().setLocation("/logout"));

        HorizontalLayout headerLayout = new HorizontalLayout(logoImage, logoText, userButton);
        headerLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        headerLayout.setWidthFull();
        headerLayout.setPadding(true);
        headerLayout.setSpacing(true);
        headerLayout.getStyle()
                .set("background-color", "#f0f4ff")
                .set("padding", "10px 20px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        addToNavbar(headerLayout);
    }

    private void createDrawer() {
        RouterLink tutores = createRouterLink("Tutores", TutorView.class, VaadinIcon.USERS);
        RouterLink eventos = createRouterLink("Eventos Académicos", EventoAcademicoView.class, VaadinIcon.CALENDAR);

        VerticalLayout drawerLayout = new VerticalLayout(tutores, eventos);
        drawerLayout.setPadding(true);
        drawerLayout.setSpacing(true);
        drawerLayout.setWidthFull();
        drawerLayout.getStyle()
                .set("background-color", "#F5F5F5")
                .set("height", "100%")
                .set("border-right", "1px solid #ccc")
                .set("padding-top", "20px");

        addToDrawer(drawerLayout);
    }

    private RouterLink createRouterLink(String text, Class<? extends Component> target, VaadinIcon icon) {
        Icon icono = icon.create();
        icono.getStyle()
                .set("margin-right", "8px")
                .set("color", "#0D47A1");

        Span label = new Span(text);
        label.getStyle().set("font-weight", "500").set("font-size", "16px");

        HorizontalLayout layout = new HorizontalLayout(icono, label);
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle()
                .set("width", "100%")
                .set("border-radius", "8px")
                .set("padding", "8px")
                .set("transition", "background-color 0.3s ease");

        RouterLink link = new RouterLink();
        link.setRoute(target);
        link.add(layout);
        link.getStyle()
                .set("text-decoration", "none")
                .set("color", "black")
                .set("width", "100%");

        return link;
    }
}