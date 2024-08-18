package com.tubiblioteca.controller.ABMCategoria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.service.Categoria.CategoriaServicio;
import com.tubiblioteca.view.Vista;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FormularioCategoriaControlador implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;

    private final Logger log = LoggerFactory.getLogger(FormularioCategoriaControlador.class);

    private Categoria categoria;
    private CategoriaServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new CategoriaServicio(AppConfig.getRepositorio());
        categoria = null;
    }

    @FXML
    private void nuevo() {
        txtNombre.clear();
    }

    @FXML
    private void guardar() {
        try {
            // Creamos un nuevo autor auxiliar
            Categoria aux = new Categoria(txtNombre.getText().trim());

            if (categoria == null) {
                categoria = aux;
                servicio.insertar(aux);
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado la categoria correctamente!");
            } else {
                // Actualizamos la categoria existente
                categoria.setNombre(aux.getNombre());
                servicio.modificar(categoria);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado la categoria correctamente!");
            }

            StageManager.cerrarModal(Vista.FormularioCategoria);
        } catch (Exception e) {
            log.error("Error al guardar la categoria.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar la categoria. " + e.getMessage());
        }
    }

    private void autocompletar() {
        txtNombre.setText(categoria.getNombre());
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        if (categoria != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public Categoria getCategoria() {
        return categoria;
    }
}