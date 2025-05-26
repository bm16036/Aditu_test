package com.aditu.view;

import com.aditu.model.Tutor;
import com.aditu.service.TutorService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import java.util.ArrayList;

@Route(value = "tutores", layout = MainView.class)
public class TutorView extends VerticalLayout {

    private final TutorService tutorService;  
    private Grid<Tutor> tutorGrid;
    private Binder<Tutor> binder;
    private Tutor tutor;

    // Campos del formulario de Tutor
    private TextField nombres = new TextField("Nombres");
    private TextField apellidos = new TextField("Apellidos");
    private TextField correoElectronico = new TextField("Correo Electrónico");
    private TextField telefono = new TextField("Teléfono");

    private Button saveButton = new Button("Agregar");
    private Button addButton = new Button("Agregar Tutor");
    private Button searchButton = new Button("Buscar");

    private TextField searchField = new TextField();

    private Dialog dialog;

    public TutorView(TutorService tutorService) {
        this.tutorService = tutorService;
        this.binder = new Binder<>(Tutor.class);

        // Usamos H3 en lugar de Text para el título (es mutable)
        H3 titulo = new H3("Tutores");

        // Aseguramos que el título sea totalmente mutable
        titulo.getStyle().set("font-size", "24px")
                         .set("font-weight", "bold")
                         .set("color", "blue");

        // Agregar el título de forma directa
        add(titulo);

        // Configuración del Grid
        tutorGrid = new Grid<>(Tutor.class);
        tutorGrid.setColumns("nombres", "apellidos", "correoElectronico", "telefono");
        
        // Columna de opciones para editar o eliminar
        tutorGrid.addComponentColumn(this::createOptionsColumn).setHeader("Opciones");

        // Rellenar el Grid con los tutores (asegurando que sea una lista mutable)
        tutorGrid.setItems(new ArrayList<>(tutorService.findAll()));  

        // Configuración de la acción de agregar tutor
        addButton.addClickListener(event -> openDialog(new Tutor()));

        // Llamada al método configureDialog() para configurar el diálogo
        configureDialog();

        // Layout para el campo de búsqueda y botones
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton, addButton);
        searchLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Centrar los elementos
        searchLayout.setSpacing(true); // Espaciado entre los elementos
        searchField.setPlaceholder("Buscar tutor"); // Placeholder en el TextField
        searchButton.addClickListener(event -> filterTutores(searchField.getValue()));

        // Añadir la sección de búsqueda, el botón de agregar y la tabla
        add(searchLayout, tutorGrid);
    }

    private void configureDialog() {
        dialog = new Dialog();
        FormLayout formLayout = new FormLayout();

        // Título del diálogo
        H3 titleTutor = new H3("Agregar Tutor");
        formLayout.add(titleTutor);

        // Alineación y espaciado de los campos
        formLayout.add(nombres, apellidos, correoElectronico, telefono);
        formLayout.setColspan(nombres, 2);
        formLayout.setColspan(apellidos, 2);
        formLayout.setColspan(correoElectronico, 2);
        formLayout.setColspan(telefono, 2);
        formLayout.setWidth("400px");

        // Botones "Limpiar" y "Cancelar"
        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        cancelButton.getStyle().set("color", "black");

        Button clearButton = new Button("Limpiar", e -> clearFormFields());
        clearButton.getStyle().set("color", "gray");

        // Alineación de los botones de "Limpiar" y "Cancelar"
        HorizontalLayout cancelAndClearButtons = new HorizontalLayout(saveButton, clearButton, cancelButton);
        cancelAndClearButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        cancelAndClearButtons.setSpacing(true);

        // Agregar los botones en el diálogo
        dialog.add(formLayout, cancelAndClearButtons);

        // Aquí vinculas los campos del formulario manualmente con el binder
        binder.forField(nombres)
              .asRequired("El nombre es obligatorio")
              .bind(Tutor::getNombres, Tutor::setNombres);
        binder.forField(apellidos)
              .asRequired("El apellido es obligatorio")
              .bind(Tutor::getApellidos, Tutor::setApellidos);
        binder.forField(correoElectronico)
              .asRequired("El correo electrónico es obligatorio")
              .withValidator(correo -> correo.contains("@"), "Correo electrónico inválido")
              .bind(Tutor::getCorreoElectronico, Tutor::setCorreoElectronico);
        
        // Validación del teléfono con formato "7777-7777"
        binder.forField(telefono)
              .asRequired("El teléfono es obligatorio")
              .withValidator(telefono -> telefono.matches("\\d{4}-\\d{4}"), 
                             "El teléfono debe tener el formato 7777-7777")
              .bind(Tutor::getTelefono, Tutor::setTelefono);

        saveButton.addClickListener(event -> {
            if (binder.isValid()) {
                tutorService.save(tutor);
                updateList();
                Notification.show("Tutor guardado con éxito");
                dialog.close();
            } else {
                Notification.show("Por favor, complete los campos obligatorios", 3000, Notification.Position.MIDDLE);
            }
        });
    }

    private HorizontalLayout createOptionsColumn(Tutor tutor) {
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addClickListener(e -> openDialog(tutor));

        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.getStyle().set("color", "white").set("background-color", "red");
        deleteButton.addClickListener(e -> deleteTutorWithConfirmation(tutor));

        return new HorizontalLayout(editButton, deleteButton);
    }

    private void openDialog(Tutor tutor) {
        this.tutor = tutor;  // Asigna la instancia de tutor al objeto tutor local
        binder.setBean(tutor);  // Vincula el tutor con el binder

        // Cambiar el texto del botón en función de si se está agregando o editando
        if (tutor.getId() == null) {
            saveButton.setText("Agregar");
        } else {
            saveButton.setText("Actualizar");
        }

        dialog.open();
    }

    private void updateList() {
        tutorGrid.setItems(new ArrayList<>(tutorService.findAll()));  // Ensure list is mutable
    }

    private void deleteTutorWithConfirmation(Tutor tutor) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Eliminación");

        VerticalLayout content = new VerticalLayout();
        content.add(new Text("¿Está seguro de que desea eliminar este tutor?"));
        confirmDialog.add(content);

        Button confirmButton = new Button("Sí, Eliminar", e -> {
            deleteTutor(tutor);
            confirmDialog.close();
        });

        confirmButton.getStyle().set("color", "white");
        confirmButton.getStyle().set("background-color", "red");

        Button cancelButton = new Button("Cancelar", e -> confirmDialog.close());
        cancelButton.getStyle().set("color", "black");

        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        confirmDialog.getFooter().add(buttonsLayout);
        confirmDialog.open();
    }

    private void deleteTutor(Tutor tutor) {
        tutorService.delete(tutor.getId());
        updateList();
        Notification.show("Tutor eliminado con éxito");
    }

    private void filterTutores(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            tutorGrid.setItems(new ArrayList<>(tutorService.findAll()));  
        } else {
            tutorGrid.setItems(new ArrayList<>(tutorService.findByNombresOrApellidos(searchTerm)));
        }
    }

    private void clearFormFields() {
        nombres.clear();
        apellidos.clear();
        correoElectronico.clear();
        telefono.clear();
    }
}
