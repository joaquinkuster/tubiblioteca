package com.tubiblioteca.helper;

import java.time.LocalDate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Prestamo;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;

public class ControlUI {

    // Configurar Spinner para permitir solo números entre 1 y 100
    public static void configurarSpinnerCantidad(Spinner<Integer> spinner) {
        // Configurar el Spinner con un rango de 1 a 100 y un valor inicial de 1
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Crear un patrón para permitir solo números enteros de hasta 3 dígitos
        Pattern validIntPattern = Pattern.compile("\\d{0,3}");

        // Crear un filtro para el TextFormatter que permita solo números enteros dentro del rango 1-100
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (validIntPattern.matcher(newText).matches()) {
                try {
                    int value = Integer.parseInt(newText);
                    if (value >= 1 && value <= 100) {
                        return change; // Acepta el cambio si es válido
                    }
                } catch (NumberFormatException e) {
                    // Si el texto no es un número válido, rechaza el cambio
                    return null;
                }
            }
            return null; // Rechaza el cambio si no coincide con el patrón o el rango
        };

        // Aplicar el filtro al editor del Spinner
        spinner.getEditor().setTextFormatter(new TextFormatter<>(filter));
    }

    // Configurar DatePicker para validar el formato de la fecha cuando se pierde el foco
    public static void configurarDatePicker(DatePicker... datePickers) {
        for (DatePicker datePicker : datePickers) {
            // Patrón para validar la entrada en formato dd/MM/yyyy
            Pattern pattern = Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$");

            // Agregar un listener para validar la entrada cuando se pierde el foco
            datePicker.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) { // Cuando se pierde el foco
                    String enteredText = datePicker.getEditor().getText();
                    if (!pattern.matcher(enteredText).matches()) {
                        datePicker.getEditor().setText(""); // Borra el texto si no es válido
                    }
                }
            });
        }
    }

    // Configurar celdas de TableColumn para mostrar fechas en formato personalizado
    @SafeVarargs
    public static void configurarCeldaFecha(TableColumn<Prestamo, LocalDate>... columnas) {
        for (TableColumn<Prestamo, LocalDate> columna : columnas) {
            // Establecemos una fábrica de celdas personalizada para la columna
            columna.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    if (fecha == null || empty) {
                        setText(!empty ? "No hay fecha disponible." : null);
                    } else {
                        // Formateamos la fecha antes de mostrarla en la celda
                        setText(Fecha.formatearFecha(fecha));
                    }
                }
            });
        }
    }

    // Configurar celdas de TableColumn para mostrar el nombre y apellido de un miembro
    public static void configurarCeldaNombreApellido(TableColumn<Miembro, String> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nombre, boolean empty) {
                super.updateItem(nombre, empty);
                if (nombre == null || empty) {
                    setText(null);
                } else {
                    // Obtener el miembro correspondiente a la fila actual y mostrar su nombre completo
                    Miembro miembro = getTableView().getItems().get(getIndex());
                    setText(nombre + " " + miembro.getApellido());
                }
            }
        });
    }

    // Limitar la longitud de un texto a un máximo especificado, añadiendo "..." si es necesario
    public static String limitar(String texto, int maxLength) {
        if (texto.length() > maxLength) {
            return texto.substring(0, maxLength) + "...";
        }
        return texto;
    }

    // Configurar un botón para activarse con la tecla Enter
    public static void configurarAtajoTecladoEnter(Button btn) {
        Platform.runLater(() -> {
            Scene escena = btn.getScene();
            if (escena != null) {
                // Asignar acción de tecla Enter para el botón
                escena.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        btn.fire();
                    }
                });
            }
        });
    }

    // Desactivar y ocultar uno o más controles
    public static void desactivarControl(Control... controles) {
        for (Control control : controles) {
            control.setDisable(true);
            control.setVisible(false);
        }
    }
}