package com.tubiblioteca.controller.ABMCopiaLibro;

import javafx.beans.property.SimpleStringProperty;
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
import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.EstadoCopiaLibro;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.model.Rack;
import com.tubiblioteca.model.TipoCopiaLibro;
import com.tubiblioteca.service.CopiaLibro.CopiaLibroServicio;
import com.tubiblioteca.service.Libro.LibroServicio;
import com.tubiblioteca.service.Rack.RackServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.Selector;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListaCopiasLibrosControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<CopiaLibro, TipoCopiaLibro> colTipo;
    @FXML
    private TableColumn<CopiaLibro, EstadoCopiaLibro> colEstado;
    @FXML
    private TableColumn<CopiaLibro, Double> colPrecio;
    @FXML
    private TableColumn<CopiaLibro, Libro> colLibro;
    @FXML
    private TableColumn<CopiaLibro, Rack> colRack;
    @FXML
    private TableColumn<CopiaLibro, String> colReferencia;

    // Tabla de miembros
    @FXML
    private TableView<CopiaLibro> tblCopias;

    // Filtros adicionales
    @FXML
    private SearchableComboBox<TipoCopiaLibro> cmbTipo;
    @FXML
    private SearchableComboBox<EstadoCopiaLibro> cmbEstado;
    @FXML
    private TextField txtPrecio;
    @FXML
    private SearchableComboBox<Libro> cmbLibro;
    @FXML
    private SearchableComboBox<Rack> cmbRack;
    @FXML
    private CheckBox checkReferencia;

    // Listas utilizadas
    private final ObservableList<CopiaLibro> copias = FXCollections.observableArrayList();
    private final ObservableList<CopiaLibro> filtrados = FXCollections.observableArrayList();
    private final ObservableList<TipoCopiaLibro> tipos = FXCollections.observableArrayList();
    private final ObservableList<EstadoCopiaLibro> estados = FXCollections.observableArrayList();
    private final ObservableList<Libro> libros = FXCollections.observableArrayList();
    private final ObservableList<Rack> racks = FXCollections.observableArrayList();

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaCopiasLibrosControlador.class);

    private CopiaLibroServicio servicio;
    private Pair<FormularioCopiaLibroControlador, Parent> formulario;
    private LibroServicio servicioLibro;
    private RackServicio servicioRack;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
        inicializarFiltros();
    }

    private void inicializarTabla() {
        try {
            var repositorio = AppConfig.getRepositorio();
            servicio = new CopiaLibroServicio(repositorio);
            servicioLibro = new LibroServicio(repositorio);
            servicioRack = new RackServicio(repositorio);

            colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
            colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
            colLibro.setCellValueFactory(new PropertyValueFactory<>("libro"));
            colRack.setCellValueFactory(new PropertyValueFactory<>("rack"));
            colReferencia.setCellValueFactory(cellData -> {
                CopiaLibro copia = cellData.getValue();
                String referencia = copia.isReferencia() ? "Sí" : "No";
                return new SimpleStringProperty(referencia);
            });

            copias.clear();
            filtrados.clear();
            copias.addAll(servicio.buscarTodos());
            filtrados.addAll(copias);
            tblCopias.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de copias: ", e);
        }
    }

    private void inicializarFiltros() {
        tipos.clear();
        tipos.addAll(TipoCopiaLibro.values());
        cmbTipo.setItems(tipos);

        estados.clear();
        estados.addAll(EstadoCopiaLibro.values());
        cmbEstado.setItems(estados);

        libros.clear();
        libros.addAll(servicioLibro.buscarTodos());
        cmbLibro.setItems(libros);

        racks.clear();
        racks.addAll(servicioRack.buscarTodos());
        cmbRack.setItems(racks);
    }

    @FXML
    private void modificar() {
        CopiaLibro copia = tblCopias.getSelectionModel().getSelectedItem();

        if (copia == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una copia de un libro!");
        } else {
            try {
                abrirFormulario(copia);
                if (copia != null && quitarFiltro(copia)) {
                    filtrados.remove(copia);
                }
                tblCopias.refresh();
            } catch (Exception e) {
                log.error("Error al modificar la copia: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            List<CopiaLibro> nuevasCopias = abrirFormulario(null);
            if (nuevasCopias != null) {
                for (CopiaLibro copia : nuevasCopias) {
                    copias.add(copia);
                    if (aplicarFiltro(copia)) {
                        filtrados.add(copia);
                    }
                }
                tblCopias.refresh();
            }
        } catch (Exception e) {
            log.error("Error al agregar las copias: ", e);
        }
    }

    @FXML
    private void eliminar() {
        CopiaLibro copia = tblCopias.getSelectionModel().getSelectedItem();

        if (copia == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una copia de un libro!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la copia del libro?")) {
            try {
                servicio.validarYBorrar(copia);
                copias.remove(copia);
                filtrados.remove(copia);
                Alerta.mostrarMensaje(false, "Info", "Copia del libro eliminada correctamente!");
                tblCopias.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar la copia.");
                Alerta.mostrarMensaje(true, "Error", e.getMessage());
            }
        }
    }

    private List<CopiaLibro> abrirFormulario(CopiaLibro copiaInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioCopiaLibro.getRutaFxml());
        FormularioCopiaLibroControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (copiaInicial != null) {
            controladorFormulario.setCopiaInicial(copiaInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioCopiaLibro);
        return controladorFormulario.getCopias();
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        copias.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblCopias.refresh();
    }

    private boolean aplicarFiltro(CopiaLibro copia) {
        TipoCopiaLibro tipo = cmbTipo.getValue();
        EstadoCopiaLibro estado = cmbEstado.getValue();
        String precio = txtPrecio.getText().trim().toLowerCase();
        Libro libro = cmbLibro.getValue();
        Rack rack = cmbRack.getValue();

        return (tipo == null || tipo.equals(copia.getTipo()))
                && (estado == null || estado.equals(copia.getEstado()))
                && (precio == null || String.valueOf(copia.getPrecio()).toLowerCase().startsWith(precio))
                && (libro == null || libro.equals(copia.getLibro()))
                && (rack == null || rack.equals(copia.getRack()))
                && (!checkReferencia.isSelected() || copia.isReferencia());
    }

    private boolean quitarFiltro(CopiaLibro copia) {
        TipoCopiaLibro tipo = cmbTipo.getValue();
        EstadoCopiaLibro estado = cmbEstado.getValue();
        String precio = txtPrecio.getText().trim().toLowerCase();
        Libro libro = cmbLibro.getValue();
        Rack rack = cmbRack.getValue();

        return (tipo != null && !tipo.equals(copia.getTipo()))
                || (estado != null && !estado.equals(copia.getEstado()))
                || (precio != null && !String.valueOf(copia.getPrecio()).toLowerCase().startsWith(precio))
                || (libro != null && !libro.equals(copia.getLibro()))
                || (rack != null && !rack.equals(copia.getRack()))
                || (checkReferencia.isSelected() && !copia.isReferencia());
    }

    @FXML
    private void limpiarFiltros() {
        cmbTipo.setValue(null);
        cmbEstado.setValue(null);
        txtPrecio.clear();
        cmbLibro.setValue(null);
        cmbRack.setValue(null);
        filtrar();
    }

    @FXML
    private void buscarLibro() {
        Libro libro = Selector.seleccionarLibro(cmbLibro.getValue());
        if (libro != null) {
            cmbLibro.setValue(libro);
        }
    }

    @FXML
    private void verificarReferencias() {
        try {
            String respuesta = servicio.verificarReferencias(libros);
            Alerta.mostrarMensaje(false, "Info", respuesta);
        } catch (Exception e) {
            Alerta.mostrarMensaje(true, "Error", e.getMessage());
        }
    }
}