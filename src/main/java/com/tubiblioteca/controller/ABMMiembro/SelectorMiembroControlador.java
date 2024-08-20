package com.tubiblioteca.controller.ABMMiembro;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.view.Vista;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectorMiembroControlador implements Initializable {

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
    private ComboBox<TipoMiembro> cmbTipo;

    // Listas utilizadas
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<Miembro> filtrados = FXCollections.observableArrayList();
    private final ObservableList<TipoMiembro> tipos = FXCollections.observableArrayList();

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaMiembrosControlador.class);

    private MiembroServicio servicio;
    Miembro miembro;

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
            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

            ControlUI.configurarCeldaNombreApellido(colNombre);

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
    private void confirmar() {
        // Obtenemos el miembro seleccionado
        Miembro miembro = tblMiembros.getSelectionModel().getSelectedItem();

        // Verificamos que sea diferente de nulo
        if (miembro == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un miembro de la biblioteca!");
        } else {
            this.miembro = miembro;
            // Salimos del modal
            StageManager.cerrarModal(Vista.SelectorMiembro);
        }
    }

    public void setMiembro(Miembro miembro) {
        if (miembro != null) {
            tblMiembros.getSelectionModel().select(miembro);
        }
    }

    public Miembro getMiembro(){
        return miembro;
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
        return (dni == null || miembro.getDni().toLowerCase().contains(dni))
                && (nombre == null || miembro.toString().toLowerCase().contains(nombre))
                && (tipo == null || tipo.equals(miembro.getTipo()));
    }

    @FXML
    private void limpiarFiltros() {
        txtDni.clear();
        txtNombre.clear();
        cmbTipo.setValue(null);
        filtrar();
    }
}