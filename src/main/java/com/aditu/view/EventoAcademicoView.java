package com.aditu.view;

import com.aditu.model.EventoAcademico;
import com.aditu.model.TipoEvento;
import com.aditu.model.Tutor;
import com.aditu.service.EventoAcademicoService;
import com.aditu.service.TutorService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;

@Route(value = "eventos-academicos", layout = MainView.class)
public class EventoAcademicoView extends VerticalLayout {

    private final EventoAcademicoService eventoService;
    private final TutorService tutorService;

    private Grid<EventoAcademico> grid = new Grid<>(EventoAcademico.class);
    private final Binder<EventoAcademico> binder = new Binder<>(EventoAcademico.class);
    private EventoAcademico evento;

    private final TextField titulo = new TextField("Título");
    private final ComboBox<TipoEvento> tipoEvento = new ComboBox<>("Tipo de Evento");
    private final DateTimePicker fechaHora = new DateTimePicker("Fecha y Hora");
    private final ComboBox<Tutor> tutorComboBox = new ComboBox<>("Tutor");

    private final Button saveButton = new Button("Guardar");
    private final Button addButton = new Button("Nuevo Evento");
    private final TextField searchField = new TextField();

    private Dialog dialog;
    private H3 dialogTitle;

    public EventoAcademicoView(EventoAcademicoService eventoService, TutorService tutorService) {
        this.eventoService = eventoService;
        this.tutorService = tutorService;

        H3 tituloPrincipal = new H3("Eventos Académicos");
        tituloPrincipal.getStyle()
            .set("font-size", "24px")
            .set("font-weight", "bold")
            .set("color", "blue");

        add(tituloPrincipal);

        configureGrid();
        configureDialog();

        searchField.setPlaceholder("Buscar evento...");
        Button searchButton = new Button("Buscar", e -> updateList());
        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton, addButton);
        searchLayout.setSpacing(true);

        add(searchLayout, grid);
        updateList();

        addButton.addClickListener(e -> openDialog(new EventoAcademico(), false));
    }

    private void configureGrid() {
        grid.removeAllColumns();
        grid.addColumn(EventoAcademico::getTitulo).setHeader("Título");
        grid.addColumn(EventoAcademico::getTipoEvento).setHeader("Tipo de Evento");
        grid.addColumn(e -> e.getFechaHora() != null ? e.getFechaHora().toString() : "").setHeader("Fecha y Hora");
        grid.addColumn(e -> e.getTutor() != null ? e.getTutor().getNombres() + " " + e.getTutor().getApellidos() : "Sin Tutor").setHeader("Tutor");
        grid.addColumn(e -> e.isNotificacionEnviada() ? "Notificado" : "Pendiente").setHeader("Notificación");
        grid.addComponentColumn(this::createOptionsColumn).setHeader("Opciones");
    }

    private HorizontalLayout createOptionsColumn(EventoAcademico evento) {
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        Button notifyButton = new Button(new Icon(VaadinIcon.BELL));
        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));

        if (evento.isNotificacionEnviada()) {
            notifyButton.getElement().getStyle().set("color", "green");
        }

        notifyButton.setTooltipText("Marcar como notificado");

        editButton.addClickListener(e -> openDialog(evento, true));

        notifyButton.addClickListener(e -> {
            evento.setNotificacionEnviada(true);
            eventoService.save(evento);
            updateList();
            Notification.show("Evento notificado");
        });

        deleteButton.getStyle().set("background-color", "red").set("color", "white");
        deleteButton.addClickListener(e -> deleteEventoWithConfirmation(evento));

        return new HorizontalLayout(editButton, notifyButton, deleteButton);
    }

    private void configureDialog() {
        dialog = new Dialog();
        dialog.setWidth("500px");

        FormLayout formLayout = new FormLayout();

        dialogTitle = new H3("Agregar Evento");
        formLayout.add(dialogTitle);

        tipoEvento.setItems(TipoEvento.values());
        tutorComboBox.setItems(tutorService.findAll());
        tutorComboBox.setItemLabelGenerator(t -> t.getNombres() + " " + t.getApellidos());

        formLayout.add(titulo, tipoEvento, fechaHora, tutorComboBox);
        formLayout.setColspan(titulo, 2);
        formLayout.setColspan(tipoEvento, 2);
        formLayout.setColspan(fechaHora, 2);
        formLayout.setColspan(tutorComboBox, 2);

        Button clearButton = new Button("Limpiar", e -> clearForm());
        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        cancelButton.getStyle().set("color", "black");

        saveButton.addClickListener(e -> {
            try {
                binder.writeBean(evento);
                eventoService.save(evento);
                updateList();
                dialog.close();
                Notification.show("Evento guardado correctamente");
            } catch (ValidationException ex) {
                Notification.show("Complete todos los campos requeridos", 3000, Notification.Position.MIDDLE);
            }
        });

        binder.bind(titulo, EventoAcademico::getTitulo, EventoAcademico::setTitulo);
        binder.bind(tipoEvento, EventoAcademico::getTipoEvento, EventoAcademico::setTipoEvento);
        binder.bind(fechaHora, EventoAcademico::getFechaHora, EventoAcademico::setFechaHora);
        binder.bind(tutorComboBox, EventoAcademico::getTutor, EventoAcademico::setTutor);

        // Validaciones
        binder.forField(titulo)
              .withValidator(t -> !t.isEmpty(), "El título no puede estar vacío")
              .bind(EventoAcademico::getTitulo, EventoAcademico::setTitulo);

        binder.forField(tipoEvento)
              .withValidator(t -> t != null, "El tipo de evento es obligatorio")
              .bind(EventoAcademico::getTipoEvento, EventoAcademico::setTipoEvento);

        binder.forField(fechaHora)
              .withValidator(t -> t != null && t.isAfter(java.time.LocalDateTime.now()), "La fecha debe ser futura")
              .bind(EventoAcademico::getFechaHora, EventoAcademico::setFechaHora);

        binder.forField(tutorComboBox)
              .withValidator(t -> t != null, "Debe seleccionar un tutor")
              .bind(EventoAcademico::getTutor, EventoAcademico::setTutor);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, clearButton, cancelButton);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        dialog.add(formLayout, buttonLayout);
    }

    private void openDialog(EventoAcademico evento, boolean isEdit) {
        this.evento = evento;
        binder.readBean(evento);

        dialogTitle.setText(isEdit ? "Actualizar Evento" : "Agregar Evento");
        saveButton.setText(isEdit ? "Actualizar" : "Guardar");

        dialog.open();
    }

    private void updateList() {
        String filtro = searchField.getValue();
        if (filtro == null || filtro.isEmpty()) {
            grid.setItems(new ArrayList<>(eventoService.findAll()));
        } else {
            grid.setItems(eventoService.findAll().stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(filtro.toLowerCase()))
                .toList());
        }
    }

    private void deleteEventoWithConfirmation(EventoAcademico evento) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Eliminación");

        VerticalLayout content = new VerticalLayout();
        content.add(new Text("¿Está seguro de que desea eliminar este evento?"));
        confirmDialog.add(content);

        Button confirmButton = new Button("Sí, Eliminar", e -> {
            eventoService.delete(evento.getId());
            updateList();
            confirmDialog.close();
            Notification.show("Evento eliminado");
        });

        confirmButton.getStyle().set("color", "white").set("background-color", "red");

        Button cancelButton = new Button("Cancelar", e -> confirmDialog.close());
        cancelButton.getStyle().set("color", "black");

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        confirmDialog.getFooter().add(buttons);
        confirmDialog.open();
    }

    private void clearForm() {
        titulo.clear();
        tipoEvento.clear();
        fechaHora.clear();
        tutorComboBox.clear();
    }
}
