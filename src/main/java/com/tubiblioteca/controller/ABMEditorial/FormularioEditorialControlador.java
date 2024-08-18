package com.tubiblioteca.controller.ABMEditorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.model.Editorial;
import com.tubiblioteca.service.Editorial.EditorialServicio;
import com.tubiblioteca.view.Vista;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FormularioEditorialControlador implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;

    private final Logger log = LoggerFactory.getLogger(FormularioEditorialControlador.class);

    private Editorial editorial;
    private EditorialServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new EditorialServicio(AppConfig.getRepositorio());
        editorial = null;
    }

    @FXML
    private void nuevo() {
        txtNombre.clear();
    }

    @FXML
    private void guardar() {
        try {
            // Creamos un nuevo editorial auxiliar
            Editorial aux = new Editorial(txtNombre.getText().trim());

            if (editorial == null) {
                // Validamos si el DNI ya está en uso
                editorial = aux;
                servicio.insertar(aux);
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado la editorial correctamente!");
            } else {
                // Actualizamos el miembro existente
                editorial.setNombre(aux.getNombre());

                servicio.modificar(editorial);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado la editorial correctamente!");
            }

            StageManager.cerrarModal(Vista.FormularioEditorial);
        } catch (Exception e) {
            log.error("Error al guardar la editorial.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar la editorial. " + e.getMessage());
        }
    }

    private void autocompletar() {
        txtNombre.setText(editorial.getNombre());
    }

    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
        if (editorial != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public Editorial getEditorial() {
        return editorial;
    }
}