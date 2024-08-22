package com.tubiblioteca.controller.ABMCategoria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.service.Categoria.CategoriaServicio;
import com.tubiblioteca.view.Vista;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FormularioCategoriaControlador implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final Logger log = LoggerFactory.getLogger(FormularioCategoriaControlador.class);

    private Categoria categoriaInicial;
    private ObservableList<Categoria> nuevasCategorias = FXCollections.observableArrayList();
    private CategoriaServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new CategoriaServicio(AppConfig.getRepositorio());
        categoriaInicial = null;
        nuevasCategorias.clear();
        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    @FXML
    private void nuevo() {
        txtNombre.clear();
    }

    @FXML
    private void guardar() {
        try {
 
            String nombre = txtNombre.getText().trim();

            if (categoriaInicial == null) {
                nuevasCategorias.add(servicio.validarEInsertar(nombre));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado la categoria correctamente!");
            } else {
                servicio.validarYModificar(categoriaInicial, nombre);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado la categoria correctamente!");
                StageManager.cerrarModal(Vista.FormularioCategoria);
            }
        } catch (Exception e) {
            log.error("Error al guardar la categoria.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar la categoria. \n" + e.getMessage());
        }
    }

    private void autocompletar() {
        txtNombre.setText(categoriaInicial.getNombre());
    }

    public void setCategoriaInicial(Categoria categoria) {
        this.categoriaInicial = categoria;
        if (categoria != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public List<Categoria> getCategorias() {
        return nuevasCategorias;
    }
}