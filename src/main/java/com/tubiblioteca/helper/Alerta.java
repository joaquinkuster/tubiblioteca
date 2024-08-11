package com.tubiblioteca.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import java.util.ArrayList;

public class Alerta {

    private static final Alert.AlertType ERROR = Alert.AlertType.ERROR;
    private static final Alert.AlertType INFO = Alert.AlertType.INFORMATION;

    private static final String BULLET = "●";

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

    public static String convertirCadenaErrores(ArrayList<String> mensajes) {
        if (!mensajes.isEmpty()) {
            // Concatenamos cada error en un sola cadena
            StringBuilder cadenaMensajes = new StringBuilder();
            cadenaMensajes.append("Se ha producido uno o varios errores:").append("\n");
            for (String msj : mensajes) {
                cadenaMensajes.append("\n" + BULLET + " ").append(msj).append("\n");
            }

            // Finalmente, retornamos los errores en pantalla
            return cadenaMensajes.toString();
        } else {
            return null;
        }
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
