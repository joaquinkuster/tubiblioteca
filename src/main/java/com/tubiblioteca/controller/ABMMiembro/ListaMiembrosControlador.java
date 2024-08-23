package com.tubiblioteca.controller.ABMMiembro;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import org.controlsfx.control.SearchableComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListaMiembrosControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Miembro, String> colDni;
    @FXML
    private TableColumn<Miembro, String> colNombre;
    @FXML
    private TableColumn<Miembro, TipoMiembro> colTipo;

    // Tabla de miembros
    @FXML
    private TableView<Miembro> tblMiembros;

    // Filtros adicionales
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private SearchableComboBox<TipoMiembro> cmbTipo;

    // Listas utilizadas
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<Miembro> filtrados = FXCollections.observableArrayList();
    private final ObservableList<TipoMiembro> tipos = FXCollections.observableArrayList();

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaMiembrosControlador.class);

    private MiembroServicio servicio;
    private Pair<FormularioMiembroControlador, Parent> formulario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
        inicializarFiltros();
    }

    private void inicializarTabla() {
        try {
            servicio = new MiembroServicio(AppConfig.getRepositorio());

            colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            ControlUI.configurarCeldaNombreApellido(colNombre);
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

            miembros.clear();
            filtrados.clear();
            miembros.addAll(servicio.buscarTodos());
            filtrados.addAll(miembros);
            tblMiembros.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de miembros: ", e);
        }
    }

    private void inicializarFiltros() {
        tipos.clear();
        tipos.addAll(TipoMiembro.values());
        cmbTipo.setItems(tipos);
    }

    @FXML
    private void modificar() {
        Miembro miembro = tblMiembros.getSelectionModel().getSelectedItem();

        if (miembro == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un miembro de la biblioteca!");
        } else if (miembro.getTipo() != TipoMiembro.Usuario) {
            Alerta.mostrarMensaje(true, "Error",
                    "Sólo se le permite modificar la información de usuarios. Selecciona un miembro que sea un usuario.");
        } else {
            try {
                abrirFormulario(miembro);
                if (miembro != null && quitarFiltro(miembro)) {
                    filtrados.remove(miembro);
                }
                tblMiembros.refresh();
            } catch (Exception e) {
                log.error("Error al modificar el miembro: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            List<Miembro> nuevosMiembros = abrirFormulario(null);
            if (nuevosMiembros != null) {
                for (Miembro miembro : nuevosMiembros) {
                    miembros.add(miembro);
                    if (aplicarFiltro(miembro)) {
                        filtrados.add(miembro);
                    }
                }
                tblMiembros.refresh();
            }
        } catch (Exception e) {
            log.error("Error al agregar un miembro: ", e);
        }
    }

    @FXML
    private void eliminar() {
        Miembro miembro = tblMiembros.getSelectionModel().getSelectedItem();

        if (miembro == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un miembro de la biblioteca!");
        } else if (miembro.getTipo() != TipoMiembro.Usuario) {
            Alerta.mostrarMensaje(true, "Error",
                    "Sólo se le permite eliminar usuarios. Selecciona un miembro que sea un usuario.");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el miembro de la biblioteca?")) {
            try {
                servicio.borrar(miembro);
                miembros.remove(miembro);
                filtrados.remove(miembro);
                Alerta.mostrarMensaje(false, "Info", "Miembro de la biblioteca eliminado correctamente!");
                tblMiembros.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar el miembro: ", e);
                Alerta.mostrarMensaje(true, "Error",
                        "No se pudo eliminar el miembro. Puede estar vinculado a otros registros.");
            }
        }
    }

    private List<Miembro> abrirFormulario(Miembro miembroInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioMiembro.getRutaFxml());
        FormularioMiembroControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (miembroInicial != null) {
            controladorFormulario.setMiembroInicial(miembroInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioMiembro);
        return controladorFormulario.getMiembros();
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        miembros.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblMiembros.refresh();
    }

    private boolean aplicarFiltro(Miembro miembro) {
        String dni = txtDni.getText().trim().toLowerCase();
        String nombre = txtNombre.getText().trim().toLowerCase();
        TipoMiembro tipo = cmbTipo.getValue();
        return (dni == null || miembro.getDni().toLowerCase().startsWith(dni))
                && (nombre == null || miembro.toString().toLowerCase().contains(nombre))
                && (tipo == null || tipo.equals(miembro.getTipo()));
    }

    private boolean quitarFiltro(Miembro miembro) {
        String dni = txtDni.getText().trim().toLowerCase();
        String nombre = txtNombre.getText().trim().toLowerCase();
        TipoMiembro tipo = cmbTipo.getValue();
        return (dni != null && !miembro.getDni().toLowerCase().startsWith(dni))
                || (nombre != null && !miembro.toString().toLowerCase().contains(nombre))
                || (tipo != null && !tipo.equals(miembro.getTipo()));
    }

    @FXML
    private void limpiarFiltros() {
        txtDni.clear();
        txtNombre.clear();
        cmbTipo.setValue(null);
        filtrar();
    }
}
