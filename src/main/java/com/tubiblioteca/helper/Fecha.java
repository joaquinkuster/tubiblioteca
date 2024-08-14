package com.tubiblioteca.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Fecha {

    private static final DateTimeFormatter formatoFechaSimple = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Metodos para formatear fechas

    public static String fechaHoy() { return LocalDate.now().format(formatoFechaSimple); }

    public static String formatearFechaSimple(LocalDate fecha) {
        return fecha.format(formatoFechaSimple);
    }

    public static String formatearFecha(LocalDate fecha) {
        // Definimos el formato de fecha y la fecha de hoy
        String formatoFecha = "dd 'de' MMMM 'de' yyyy";
        String dia;
        LocalDate hoy = LocalDate.now();

        // Verificamos si la fecha proporcionada es hoy, ayer o manaña
        if (fecha.equals(hoy)) {
            dia = "Hoy";
        } else if (fecha.equals(hoy.minusDays(1))) {
            dia = "Ayer";
        } else if (fecha.equals(hoy.plusDays(1))) {
            dia = "Mañana";
        } else {
            // En caso de que no sea ninguno, establecemos el día correspondiente
            dia = fecha.format(DateTimeFormatter.ofPattern("EEEE").withLocale(Locale.forLanguageTag("es")));
        }

        // Formateamos la fecha
        String fechaFormateada = dia + ", " + fecha.format(DateTimeFormatter.ofPattern(formatoFecha)
                .withLocale(Locale.forLanguageTag("es")));

        // Ponemos la primer letra en mayusculas
        fechaFormateada = fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);

        // Devolvemos la fecha formateada
        return fechaFormateada;
    }
}
