package com.tubiblioteca.controller.ABMAutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.model.Autor;
import com.tubiblioteca.service.Autor.AutorServicio;
import com.tubiblioteca.view.Vista;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FormularioAutorControlador implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;

    private final Logger log = LoggerFactory.getLogger(FormularioAutorControlador.class);

    private Autor autor;
    private AutorServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new AutorServicio(AppConfig.getRepositorio());
        autor = null;
    }

    @FXML
    private void nuevo() {
        txtNombre.clear();
    }

    @FXML
    private void guardar() {
        try {
            // Creamos un nuevo autor auxiliar
            Autor aux = new Autor(txtNombre.getText().trim());

            if (autor == null) {
                autor = aux;
                servicio.insertar(aux);
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el autor correctamente!");
            } else {
                // Actualizamos el autor existente
                autor.setNombre(aux.getNombre());
                servicio.modificar(autor);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el autor correctamente!");
            }

            StageManager.cerrarModal(Vista.FormularioAutor);
        } catch (Exception e) {
            log.error("Error al guardar el autor.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el autor. " + e.getMessage());
        }
    }

    private void autocompletar() {
        txtNombre.setText(autor.getNombre());
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
        if (autor != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public Autor getAutor() {
        return autor;
    }
}