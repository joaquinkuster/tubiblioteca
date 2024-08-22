package com.tubiblioteca.controller.ABMIdioma;

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
import com.tubiblioteca.model.Idioma;
import com.tubiblioteca.service.Idioma.IdiomaServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListaIdiomasControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Idioma, String> colNombre;

    // Tabla de idiomas
    @FXML
    private TableView<Idioma> tblIdiomas;

    // Filtros adicionales
    @FXML
    private TextField txtNombre;

    // Listas utilizadas
    private final ObservableList<Idioma> idiomas = FXCollections.observableArrayList();
    private final ObservableList<Idioma> filtrados = FXCollections.observableArrayList();

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaIdiomasControlador.class);

    private IdiomaServicio servicio;
    private Pair<FormularioIdiomaControlador, Parent> formulario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
    }

    private void inicializarTabla() {
        try {
            servicio = new IdiomaServicio(AppConfig.getRepositorio());

            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            idiomas.clear();
            filtrados.clear();
            idiomas.addAll(servicio.buscarTodos());
            filtrados.addAll(idiomas);
            tblIdiomas.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de idiomas: ", e);
        }
    }

    @FXML
    private void modificar() {
        Idioma idioma = tblIdiomas.getSelectionModel().getSelectedItem();

        if (idioma == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un idioma!");
        } else {
            try {
                abrirFormulario(idioma);
                if (idioma != null && quitarFiltro(idioma)) {
                    filtrados.remove(idioma);
                }
                tblIdiomas.refresh();
            } catch (Exception e) {
                log.error("Error al modificar el idioma: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            List<Idioma> nuevosIdiomas = abrirFormulario(null);
            if (nuevosIdiomas != null) {
                for (Idioma idioma : nuevosIdiomas) {
                    idiomas.add(idioma);
                    if (aplicarFiltro(idioma)) {
                        filtrados.add(idioma);
                    }
                }
                tblIdiomas.refresh();
            }
        } catch (Exception e) {
            log.error("Error al agregar un idioma: ", e);
        }
    }

    @FXML
    private void eliminar() {
        Idioma idioma = tblIdiomas.getSelectionModel().getSelectedItem();

        if (idioma == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un idioma!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el idioma?")) {
            try {
                servicio.borrar(idioma);
                idiomas.remove(idioma);
                filtrados.remove(idioma);
                Alerta.mostrarMensaje(false, "Info", "idioma eliminado correctamente!");
                tblIdiomas.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar el idioma: ", e);
                Alerta.mostrarMensaje(true, "Error",
                        "No se pudo eliminar el idioma. Puede estar vinculado a otros registros.");
            }
        }
    }

    private List<Idioma> abrirFormulario(Idioma idiomaInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioIdioma.getRutaFxml());
        FormularioIdiomaControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (idiomaInicial != null) {
            controladorFormulario.setIdiomaInicial(idiomaInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioIdioma);
        return controladorFormulario.getIdiomas();
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        idiomas.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblIdiomas.refresh();
    }

    private boolean aplicarFiltro(Idioma idioma) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre == null || idioma.toString().toLowerCase().contains(nombre));
    }

    private boolean quitarFiltro(Idioma idioma) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre != null && !idioma.toString().toLowerCase().contains(nombre));
    }

    @FXML
    private void limpiarFiltros() {
        txtNombre.clear();
        filtrar();
    }
}
