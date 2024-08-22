package com.tubiblioteca.controller.ABMAutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Autor;
import com.tubiblioteca.service.Autor.AutorServicio;
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

public class FormularioAutorControlador implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final Logger log = LoggerFactory.getLogger(FormularioAutorControlador.class);

    private Autor autorInicial;
    private ObservableList<Autor> nuevosAutores = FXCollections.observableArrayList();
    private AutorServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new AutorServicio(AppConfig.getRepositorio());
        autorInicial = null;
        nuevosAutores.clear();
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

            if (autorInicial == null) {
                nuevosAutores.add(servicio.validarEInsertar(nombre));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el autor correctamente!");
            } else {
                servicio.validarYModificar(autorInicial, nombre);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el autor correctamente!");
                StageManager.cerrarModal(Vista.FormularioAutor);
            }
        } catch (Exception e) {
            log.error("Error al guardar el autor.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el autor.\n" + e.getMessage());
        }
    }

    private void autocompletar() {
        txtNombre.setText(autorInicial.getNombre());
    }

    public void setAutorInicial(Autor autor) {
        this.autorInicial = autor;
        if (autor != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public List<Autor> getAutores() {
        return nuevosAutores;
    }
}