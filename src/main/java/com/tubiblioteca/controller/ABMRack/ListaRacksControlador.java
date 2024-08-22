package com.tubiblioteca.controller.ABMRack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.model.Rack;
import com.tubiblioteca.service.Rack.RackServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListaRacksControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Rack, String> colDescripcion;

    // Tabla de racks
    @FXML
    private TableView<Rack> tblRacks;

    // Filtros adicionales
    @FXML
    private TextField txtDescripcion;

    // Listas utilizadas
    private final ObservableList<Rack> racks = FXCollections.observableArrayList();
    private final ObservableList<Rack> filtrados = FXCollections.observableArrayList();

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaRacksControlador.class);

    private RackServicio servicio;
    private Pair<FormularioRackControlador, Parent> formulario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
    }

    private void inicializarTabla() {
        try {
            servicio = new RackServicio(AppConfig.getRepositorio());

            colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

            racks.clear();
            filtrados.clear();
            racks.addAll(servicio.buscarTodos());
            filtrados.addAll(racks);
            tblRacks.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de racks: ", e);
        }
    }

    @FXML
    private void modificar() {
        Rack rack = tblRacks.getSelectionModel().getSelectedItem();

        if (rack == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un rack!");
        } else {
            try {
                abrirFormulario(rack);
                if (rack != null && quitarFiltro(rack)) {
                    filtrados.remove(rack);
                }
                tblRacks.refresh();
            } catch (Exception e) {
                log.error("Error al modificar el rack: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            List<Rack> nuevosRacks = abrirFormulario(null);
            if (nuevosRacks != null) {
                for (Rack rack : nuevosRacks) {
                    racks.add(rack);
                    if (aplicarFiltro(rack)) {
                        filtrados.add(rack);
                    }
                }
                tblRacks.refresh();
            }
        } catch (Exception e) {
            log.error("Error al agregar un rack: ", e);
        }
    }

    @FXML
    private void eliminar() {
        Rack rack = tblRacks.getSelectionModel().getSelectedItem();

        if (rack == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un rack!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el rack?")) {
            try {
                servicio.borrar(rack);
                racks.remove(rack);
                filtrados.remove(rack);
                Alerta.mostrarMensaje(false, "Info", "Rack eliminado correctamente!");
                tblRacks.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar el rack: ", e);
                Alerta.mostrarMensaje(true, "Error",
                        "No se pudo eliminar el rack. Puede estar vinculado a otros registros.");
            }
        }
    }

    private List<Rack> abrirFormulario(Rack rackInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioRack.getRutaFxml());
        FormularioRackControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (rackInicial != null) {
            controladorFormulario.setRackInicial(rackInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioRack);
        return controladorFormulario.getRacks();
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        racks.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblRacks.refresh();
    }

    private boolean aplicarFiltro(Rack rack) {
        String descripcion = txtDescripcion.getText().trim().toLowerCase();
        return (descripcion == null || rack.toString().toLowerCase().contains(descripcion));
    }

    private boolean quitarFiltro(Rack rack) {
        String descripcion = txtDescripcion.getText().trim().toLowerCase();
        return (descripcion != null && !rack.toString().toLowerCase().contains(descripcion));
    }

    @FXML
    private void limpiarFiltros() {
        txtDescripcion.clear();
        filtrar();
    }
}