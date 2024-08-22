package com.tubiblioteca.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class Alerta {

    private static final Alert.AlertType ERROR = Alert.AlertType.ERROR;
    private static final Alert.AlertType INFO = Alert.AlertType.INFORMATION;

    // Metodos para mostrar mensajes en pantalla

    public static boolean mostrarConfirmacion(String titulo, String contenido) {
        // Funcion para mostrar alerta de confirmacion
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText(null);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        ButtonType resultado = alerta.showAndWait().orElse(ButtonType.CANCEL);
        return resultado == ButtonType.OK;
    }

    public static void mostrarMensaje(boolean error, String titulo, String contenido) {
        // Funcion para mostrar alerta
        Alert alert = new Alert(error ? ERROR : INFO);
        alert.setHeaderText(null);
        alert.setTitle(titulo);

        // Ajustamos el contenido
        Label contenidoAjustado = new Label(contenido);
        contenidoAjustado.setWrapText(true);
        alert.getDialogPane().setContent(contenidoAjustado);

        // Mostramos la alerta
        alert.showAndWait();
    }
}