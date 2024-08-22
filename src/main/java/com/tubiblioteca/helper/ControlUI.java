package com.tubiblioteca.helper;

import java.time.LocalDate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import com.tubiblioteca.model.Miembro;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;

public class ControlUI {

    public static void configurarSpinnerCantidad(Spinner<Integer> spinner) {
        // Configurar el Spinner con un rango de 0 a 100 y un valor inicial de 50
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Crear un patrón para permitir solo números enteros entre 1 y 100
        Pattern validIntPattern = Pattern.compile("\\d{0,3}");

        // Crear un filtro para el TextFormatter que permita solo números enteros entre
        // 1 y 100
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (validIntPattern.matcher(newText).matches()) {
                try {
                    int value = Integer.parseInt(newText);
                    if (value >= 1 && value <= 100) {
                        return change; // Acepta el cambio
                    }
                } catch (NumberFormatException e) {
                    // Si el texto no es un número válido, lo rechaza
                    return null;
                }
            }
            return null; // Rechaza el cambio si no coincide con el patrón o el rango
        };

        // Aplicar el filtro al editor del Spinner
        spinner.getEditor().setTextFormatter(new TextFormatter<>(filter));
    }

    public static void configurarDatePicker(DatePicker datePicker) {
        Pattern pattern = Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$");
        // Permitir cualquier entrada, pero agregar un listener para validar al final
        datePicker.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) { // Cuando se pierde el foco
                String enteredText = datePicker.getEditor().getText();
                if (!pattern.matcher(enteredText).matches()) {
                    datePicker.getEditor().setText(""); // Borra el texto si no es válido
                }
            }
        });
    }

    public static <T> void configurarCeldaFecha(TableColumn<T, LocalDate> columna) {
        // Establecemos una fábrica de celdas para la columna
        columna.setCellFactory(column -> {
            // Devolvemos una nueva celda para la visualizacion de datos
            return new TableCell<>() {
                // Sobreescribimos el metodo para personalizar el comportamiento de
                // actualizacion de celda
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    // Llamamos a la clase padre para cualquier inicializacion necesaria
                    super.updateItem(fecha, empty);
                    // Verificamos si la fecha es nula
                    if (fecha == null || empty) {
                        setText(!empty ? "No hay fecha disponible." : null);
                    } else {
                        // Si es diferente de nula, formateamos la fecha antes de mostrarla en la celda
                        setText(Fecha.formatearFecha(fecha));
                    }
                }
            };
        });
    }

    public static void configurarCeldaNombreApellido(TableColumn<Miembro, String> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nombre, boolean empty) {
                super.updateItem(nombre, empty);
                if (nombre == null || empty) {
                    setText(null);
                } else {
                    Miembro miembro = getTableView().getItems().get(getIndex());
                    setText(nombre + " " + miembro.getApellido());
                }
            }
        });
    }

    public static String limitar(String texto, int maxLength) {
        if (texto.length() > maxLength) {
            return texto.substring(0, maxLength) + "...";
        }
        return texto;
    }

    public static void configurarAtajoTecladoEnter(Button btn) {
        // Configurar el atajo de teclado (Enter)
        Platform.runLater(() -> {
            // Asegúrate de que la escena esté cargada y lista antes de configurar el atajo
            Scene escena = btn.getScene();
            if (escena != null) {
                escena.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        btn.fire();
                    }
                });
            }        });
    }
}
