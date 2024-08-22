package com.tubiblioteca.controller.ABMAutor;

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
import com.tubiblioteca.model.Autor;
import com.tubiblioteca.service.Autor.AutorServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListaAutoresControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Autor, String> colNombre;

    // Tabla de editoriales
    @FXML
    private TableView<Autor> tblAutores;

    // Filtros adicionales
    @FXML
    private TextField txtNombre;

    // Listas utilizadas
    private final ObservableList<Autor> autores = FXCollections.observableArrayList();
    private final ObservableList<Autor> filtrados = FXCollections.observableArrayList();

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaAutoresControlador.class);

    private AutorServicio servicio;
    private Pair<FormularioAutorControlador, Parent> formulario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
    }

    private void inicializarTabla() {
        try {
            servicio = new AutorServicio(AppConfig.getRepositorio());

            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            autores.clear();
            filtrados.clear();
            autores.addAll(servicio.buscarTodos());
            filtrados.addAll(autores);
            tblAutores.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de autores: ", e);
        }
    }

    @FXML
    private void modificar() {
        Autor autor = tblAutores.getSelectionModel().getSelectedItem();

        if (autor == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un autor!");
        } else {
            try {
                abrirFormulario(autor);
                if (autor != null && quitarFiltro(autor)) {
                    filtrados.remove(autor);
                }
                tblAutores.refresh();
            } catch (Exception e) {
                log.error("Error al modificar el autor: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            List<Autor> nuevosAutores = abrirFormulario(null);
            if (nuevosAutores != null) {
                for (Autor autor : nuevosAutores) {
                    autores.add(autor);
                    if (aplicarFiltro(autor)) {
                        filtrados.add(autor);
                    }
                }
            }
            tblAutores.refresh();
        } catch (Exception e) {
            log.error("Error al agregar un autor: ", e);
        }
    }

    @FXML
    private void eliminar() {
        Autor autor = tblAutores.getSelectionModel().getSelectedItem();

        if (autor == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un autor!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el autor?")) {
            try {
                servicio.borrar(autor);
                autores.remove(autor);
                filtrados.remove(autor);
                Alerta.mostrarMensaje(false, "Info", "Autor eliminado correctamente!");
                tblAutores.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar el autor: ", e);
                Alerta.mostrarMensaje(true, "Error",
                        "No se pudo eliminar el autor. Puede estar vinculado a otros registros.");
            }
        }
    }

    private List<Autor> abrirFormulario(Autor autorInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioAutor.getRutaFxml());
        FormularioAutorControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (autorInicial != null) {
            controladorFormulario.setAutorInicial(autorInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioAutor);
        return controladorFormulario.getAutores();
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        autores.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblAutores.refresh();
    }

    private boolean aplicarFiltro(Autor autor) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre == null || autor.toString().toLowerCase().contains(nombre));
    }

    private boolean quitarFiltro(Autor autor) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre != null && !autor.toString().toLowerCase().contains(nombre));
    }

    @FXML
    private void limpiarFiltros() {
        txtNombre.clear();
        filtrar();
    }
}
