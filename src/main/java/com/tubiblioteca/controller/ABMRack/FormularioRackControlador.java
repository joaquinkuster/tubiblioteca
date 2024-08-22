package com.tubiblioteca.controller.ABMRack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Rack;
import com.tubiblioteca.service.Rack.RackServicio;
import com.tubiblioteca.view.Vista;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FormularioRackControlador implements Initializable {

    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final Logger log = LoggerFactory.getLogger(FormularioRackControlador.class);

    private Rack rackInicial;
    private ObservableList<Rack> nuevosRacks = FXCollections.observableArrayList();
    private RackServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new RackServicio(AppConfig.getRepositorio());
        rackInicial = null;
        nuevosRacks.clear();
        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    @FXML
    private void nuevo() {
        txtDescripcion.clear();
    }

    @FXML
    private void guardar() {
        try {

            String descripcion = txtDescripcion.getText().trim();

            if (rackInicial == null) {
                nuevosRacks.add(servicio.validarEInsertar(descripcion));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el rack correctamente!");
            } else {
                servicio.validarYModificar(rackInicial, descripcion);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el rack correctamente!");
                StageManager.cerrarModal(Vista.FormularioRack);
            }
        } catch (Exception e) {
            log.error("Error al guardar el rack.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el rack.\n" + e.getMessage());
        }
    }

    private void autocompletar() {
        txtDescripcion.setText(rackInicial.getDescripcion());
    }

    public void setRackInicial(Rack rack) {
        this.rackInicial = rack;
        if (rack != null) {
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    public List<Rack> getRacks() {
        return nuevosRacks;
    }
}