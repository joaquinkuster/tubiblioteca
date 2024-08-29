package com.tubiblioteca.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class Alerta {

    // Tipos de alertas predefinidos: ERROR e INFO
    private static final Alert.AlertType ERROR = Alert.AlertType.ERROR;
    private static final Alert.AlertType INFO = Alert.AlertType.INFORMATION;

    // Métodos para mostrar mensajes en pantalla

    /**
     * Muestra una alerta de confirmación.
     *
     * @param titulo El título de la alerta.
     * @param contenido El contenido del mensaje de la alerta.
     * @return true si el usuario hace clic en "OK", false si hace clic en "CANCEL".
     */
    public static boolean mostrarConfirmacion(String titulo, String contenido) {
        // Crear una alerta de tipo CONFIRMATION
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        // Establecer el encabezado como null para que no se muestre
        alerta.setHeaderText(null);
        // Establecer el título de la alerta
        alerta.setTitle(titulo);
        // Establecer el texto del contenido de la alerta
        alerta.setContentText(contenido);
        // Mostrar la alerta y esperar la respuesta del usuario
        ButtonType resultado = alerta.showAndWait().orElse(ButtonType.CANCEL);
        // Retornar true si el usuario presionó "OK", de lo contrario false
        return resultado == ButtonType.OK;
    }

    /**
     * Muestra una alerta con un mensaje de error o de información.
     *
     * @param error Indica si es un mensaje de error (true) o de información (false).
     * @param titulo El título de la alerta.
     * @param contenido El contenido del mensaje de la alerta.
     */
    public static void mostrarMensaje(boolean error, String titulo, String contenido) {
        // Elegir el tipo de alerta basado en el valor de 'error'
        Alert alert = new Alert(error ? ERROR : INFO);
        // Establecer el encabezado como null para que no se muestre
        alert.setHeaderText(null);
        // Establecer el título de la alerta
        alert.setTitle(titulo);

        // Crear una etiqueta para el contenido y ajustar el texto para que se envuelva
        Label contenidoAjustado = new Label(contenido);
        contenidoAjustado.setWrapText(true);
        // Establecer el contenido de la alerta usando la etiqueta ajustada
        alert.getDialogPane().setContent(contenidoAjustado);

        // Mostrar la alerta y esperar que el usuario la cierre
        alert.showAndWait();
    }
}
