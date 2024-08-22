package com.tubiblioteca.controller.ABMIdioma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Idioma;
import com.tubiblioteca.service.Idioma.IdiomaServicio;
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

public class FormularioIdiomaControlador implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final Logger log = LoggerFactory.getLogger(FormularioIdiomaControlador.class);

    private Idioma idiomaInicial;
    private ObservableList<Idioma> nuevosIdiomas = FXCollections.observableArrayList();
    private IdiomaServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new IdiomaServicio(AppConfig.getRepositorio());
        idiomaInicial = null;
        nuevosIdiomas.clear();
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

            if (idiomaInicial == null) {
                nuevosIdiomas.add(servicio.validarEInsertar(nombre));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el idioma correctamente!");
            } else {
                servicio.validarYModificar(idiomaInicial, nombre);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el idioma correctamente!");
                StageManager.cerrarModal(Vista.FormularioIdioma);
            }
        } catch (Exception e) {
            log.error("Error al guardar el idioma.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el idioma.\n" + e.getMessage());
        }
    }

    private void autocompletar() {
        txtNombre.setText(idiomaInicial.getNombre());
    }

    public void setIdiomaInicial(Idioma idioma) {
        this.idiomaInicial = idioma;
        if (idioma != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public List<Idioma> getIdiomas() {
        return nuevosIdiomas;
    }
}