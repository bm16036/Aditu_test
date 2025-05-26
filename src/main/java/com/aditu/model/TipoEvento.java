package com.aditu.model;

public enum TipoEvento {
    CLASE("Clase"),
    REUNION("Reunión"),
    FECHA_IMPORTANTE("Fecha importante"),
    VACACIONES("Vacaciones"),
    DIAS_LIBRES("Días libres"),
    REUNIONES_DE_EQUIPO("Reuniones de equipo"),
    ENTRENAMIENTOS_Y_CAPACITACION("Entrenamientos y capacitación"),
    FECHAS_DE_EVALUACION("Fechas de evaluación"),
    ACTIVIDADES_EXTRACURRICULARES("Actividades extracurriculares"),
    REUNIONES_CON_PADRES("Reuniones con padres");

    private final String descripcion;

    TipoEvento(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}