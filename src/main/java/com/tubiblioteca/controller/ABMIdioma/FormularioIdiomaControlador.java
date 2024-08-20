package com.tubiblioteca.controller.ABMIdioma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.model.Idioma;
import com.tubiblioteca.service.Idioma.IdiomaServicio;
import com.tubiblioteca.view.Vista;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FormularioIdiomaControlador implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;

    private final Logger log = LoggerFactory.getLogger(FormularioIdiomaControlador.class);

    private Idioma idioma;
    private IdiomaServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new IdiomaServicio(AppConfig.getRepositorio());
        idioma = null;
    }

    @FXML
    private void nuevo() {
        txtNombre.clear();
    }

    @FXML
    private void guardar() {
        try {
            // Creamos un nuevo idioma auxiliar
            Idioma aux = new Idioma(txtNombre.getText().trim());

            if (idioma == null) {
                idioma = aux;
                servicio.insertar(aux);
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el idioma correctamente!");
            } else {
                // Actualizamos el idioma existente
                idioma.setNombre(aux.getNombre());
                servicio.modificar(idioma);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el idioma correctamente!");
            }

            StageManager.cerrarModal(Vista.FormularioIdioma);
        } catch (Exception e) {
            log.error("Error al guardar el idioma.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el idioma. " + e.getMessage());
        }
    }

    private void autocompletar() {
        txtNombre.setText(idioma.getNombre());
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
        if (idioma != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public Idioma getIdioma() {
        return idioma;
    }
}