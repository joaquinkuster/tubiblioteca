package com.tubiblioteca.controller.ABMEditorial;

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
import com.tubiblioteca.model.Editorial;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.service.Editorial.EditorialServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListaEditorialesControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Miembro, String> colNombre;

    // Tabla de editoriales
    @FXML
    private TableView<Editorial> tblEditoriales;

    // Filtros adicionales
    @FXML
    private TextField txtNombre;
    
    // Listas utilizadas
    private final ObservableList<Editorial> editoriales = FXCollections.observableArrayList();
    private final ObservableList<Editorial> filtrados = FXCollections.observableArrayList();
    
    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaEditorialesControlador.class);

    private EditorialServicio servicio;
    private Pair<FormularioEditorialControlador, Parent> formulario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
    }

    private void inicializarTabla() {
        try {
            servicio = new EditorialServicio(AppConfig.getRepositorio());

            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            editoriales.clear();
            filtrados.clear();
            editoriales.addAll(servicio.buscarTodos());
            filtrados.addAll(editoriales);
            tblEditoriales.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de editoriales: ", e);
        }
    }

    @FXML
    private void modificar() {
        Editorial editorial = tblEditoriales.getSelectionModel().getSelectedItem();

        if (editorial == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una editorial!");
        } else {
            try {
                editorial = abrirFormulario(editorial);
                if (editorial != null && quitarFiltro(editorial)) {
                    filtrados.remove(editorial);
                }
                tblEditoriales.refresh();
            } catch (Exception e) {
                log.error("Error al modificar la editorial: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            Editorial editorial = abrirFormulario(null);
            if (editorial != null) {
                editoriales.add(editorial);
                if (aplicarFiltro(editorial)) {
                    filtrados.add(editorial);
                    tblEditoriales.refresh();
                }
            }
        } catch (Exception e) {
            log.error("Error al agregar una editorial: ", e);
        }
    }

    @FXML
    private void eliminar() {
        Editorial editorial = tblEditoriales.getSelectionModel().getSelectedItem();

        if (editorial == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una editorial!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la editorial?")) {
            try {
                servicio.borrar(editorial);
                editoriales.remove(editorial);
                filtrados.remove(editorial);
                Alerta.mostrarMensaje(false, "Info", "Editorial eliminado correctamente!");
                tblEditoriales.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar la editorial: ", e);
                Alerta.mostrarMensaje(true, "Error",
                        "No se pudo eliminar la editorial. Puede estar vinculado a otros registros.");
            }
        }
    }

    private Editorial abrirFormulario(Editorial editorialInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioEditorial.getRutaFxml());
        FormularioEditorialControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (editorialInicial != null) {
            controladorFormulario.setEditorial(editorialInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioEditorial);
        return controladorFormulario.getEditorial();
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        editoriales.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblEditoriales.refresh();
    }

    private boolean aplicarFiltro(Editorial editorial) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre == null || editorial.toString().toLowerCase().contains(nombre));
    }

    private boolean quitarFiltro(Editorial editorial) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre != null && !editorial.toString().toLowerCase().contains(nombre));
    }

    @FXML
    private void limpiarFiltros() {
        txtNombre.clear();
        filtrar();
    }
}
