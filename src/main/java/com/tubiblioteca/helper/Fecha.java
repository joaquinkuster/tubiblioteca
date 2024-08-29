package com.tubiblioteca.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Fecha {

    // Formato de fecha simple: "dd/MM/yyyy"
    private static final DateTimeFormatter formatoFechaSimple = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Métodos para formatear fechas

    // Devuelve la fecha actual en el formato "dd/MM/yyyy"
    public static String fechaHoy() {
        return LocalDate.now().format(formatoFechaSimple);
    }

    // Formatea una fecha en el formato "dd/MM/yyyy"
    public static String formatearFechaSimple(LocalDate fecha) {
        return fecha.format(formatoFechaSimple);
    }

    // Formatea una fecha en un formato más descriptivo
    public static String formatearFecha(LocalDate fecha) {
        // Definimos el formato de fecha descriptivo
        String formatoFecha = "dd 'de' MMMM 'de' yyyy";
        String dia;
        LocalDate hoy = LocalDate.now();

        // Verificamos si la fecha proporcionada es hoy, ayer o mañana
        if (fecha.equals(hoy)) {
            dia = "Hoy";
        } else if (fecha.equals(hoy.minusDays(1))) {
            dia = "Ayer";
        } else if (fecha.equals(hoy.plusDays(1))) {
            dia = "Mañana";
        } else {
            // Si la fecha no es hoy, ayer ni mañana, establecemos el día de la semana
            dia = fecha.format(DateTimeFormatter.ofPattern("EEEE").withLocale(Locale.forLanguageTag("es")));
        }

        // Formateamos la fecha usando el formato descriptivo
        String fechaFormateada = dia + ", " + fecha.format(DateTimeFormatter.ofPattern(formatoFecha)
                .withLocale(Locale.forLanguageTag("es")));

        // Ponemos la primera letra en mayúsculas
        fechaFormateada = fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);

        // Devolvemos la fecha formateada
        return fechaFormateada;
    }
}
