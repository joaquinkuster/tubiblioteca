package com.tubiblioteca.controller.ABMEditorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Editorial;
import com.tubiblioteca.service.Editorial.EditorialServicio;
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

public class FormularioEditorialControlador implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final Logger log = LoggerFactory.getLogger(FormularioEditorialControlador.class);

    private Editorial editorialInicial;
    private ObservableList<Editorial> nuevasEditoriales = FXCollections.observableArrayList();
    private EditorialServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new EditorialServicio(AppConfig.getRepositorio());
        editorialInicial = null;
        nuevasEditoriales.clear();
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

            if (editorialInicial == null) {
                nuevasEditoriales.add(servicio.validarEInsertar(nombre));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado la editorial correctamente!");
            } else {
                servicio.validarYModificar(editorialInicial, nombre);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado la editorial correctamente!");
                StageManager.cerrarModal(Vista.FormularioEditorial);
            }
        } catch (Exception e) {
            log.error("Error al guardar la editorial.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar la editorial.\n" + e.getMessage());
        }
    }

    private void autocompletar() {
        txtNombre.setText(editorialInicial.getNombre());
    }

    public void setEditorialInicial(Editorial editorial) {
        this.editorialInicial = editorial;
        if (editorial != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public List<Editorial> getEditoriales() {
        return nuevasEditoriales;
    }
}