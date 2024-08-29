package com.tubiblioteca.controller.ABMCopiaLibro;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Selector;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectorCopiaLibroControlador implements Initializable {

    // Columnas de la tabla que representan los atributos de CopiaLibro
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

    // Tabla donde se mostrarán las copias de libros
    @FXML
    private TableView<CopiaLibro> tblCopias;

    // Controles para filtros adicionales
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

    // Listas utilizadas para almacenar datos
    private final ObservableList<CopiaLibro> copias = FXCollections.observableArrayList();
    private final ObservableList<CopiaLibro> filtrados = FXCollections.observableArrayList();
    private final ObservableList<TipoCopiaLibro> tipos = FXCollections.observableArrayList();
    private final ObservableList<EstadoCopiaLibro> estados = FXCollections.observableArrayList();
    private final ObservableList<Libro> libros = FXCollections.observableArrayList();
    private final ObservableList<Rack> racks = FXCollections.observableArrayList();

    // Logger para registrar eventos y errores
    private final Logger log = LoggerFactory.getLogger(SelectorCopiaLibroControlador.class);

    // Servicios para interactuar con la base de datos
    private CopiaLibroServicio servicio;
    private LibroServicio servicioLibro;
    private RackServicio servicioRack;

    // Copia de libro seleccionada
    private CopiaLibro copiaLibro;

    @FXML
    private Button btnConfirmar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla(); // Configura y llena la tabla
        inicializarFiltros(); // Configura los filtros disponibles
        ControlUI.configurarAtajoTecladoEnter(btnConfirmar); // Configura el atajo de teclado para el botón de confirmar
    }

    private void inicializarTabla() {
        try {
            // Inicializa los servicios necesarios con el repositorio
            var repositorio = AppConfig.getRepositorio();
            servicio = new CopiaLibroServicio(repositorio);
            servicioLibro = new LibroServicio(repositorio);
            servicioRack = new RackServicio(repositorio);

            // Configura las columnas de la tabla para mostrar los datos
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

            // Llena la tabla con los datos iniciales
            copias.clear();
            filtrados.clear();
            copias.addAll(servicio.buscarTodos());
            filtrados.addAll(copias);
            tblCopias.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de copias: ", e); // Registra un error si ocurre una excepción
        }
    }

    private void inicializarFiltros() {
        // Configura las listas de filtros
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
    private void confirmar() {
        // Obtiene la copia seleccionada de la tabla
        CopiaLibro copia = tblCopias.getSelectionModel().getSelectedItem();

        // Verifica que se haya seleccionado una copia
        if (copia == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una copia de un libro!"); // Muestra un mensaje de error si no se selecciona una copia
        } else {
            copiaLibro = copia; // Asigna la copia seleccionada
            StageManager.cerrarModal(Vista.SelectorCopiaLibro); // Cierra el modal
        }
    }

    public void setCopiaLibro(CopiaLibro copiaLibro) {
        if (copiaLibro != null) {
            tblCopias.getSelectionModel().select(copiaLibro); // Selecciona la copia de libro pasada como parámetro
        }
    }

    public CopiaLibro getCopiaLibro(){
        return copiaLibro; // Retorna la copia de libro seleccionada
    }

    @FXML
    private void filtrar() {
        // Aplica los filtros a la lista de copias y actualiza la tabla
        filtrados.clear();
        copias.stream()
                .filter(this::aplicarFiltro) // Filtra las copias según los criterios especificados
                .forEach(filtrados::add); // Agrega las copias filtradas a la lista
        tblCopias.refresh(); // Refresca la tabla para mostrar los resultados filtrados
    }

    private boolean aplicarFiltro(CopiaLibro copia) {
        // Aplica los filtros a una copia de libro
        TipoCopiaLibro tipo = cmbTipo.getValue();
        EstadoCopiaLibro estado = cmbEstado.getValue();
        String precio = txtPrecio.getText().trim().toLowerCase();
        Libro libro = cmbLibro.getValue();
        Rack rack = cmbRack.getValue();

        // Verifica si la copia cumple con todos los filtros aplicados
        return (tipo == null || tipo.equals(copia.getTipo()))
                && (estado == null || estado.equals(copia.getEstado()))
                && (precio == null || String.valueOf(copia.getPrecio()).toLowerCase().startsWith(precio))
                && (libro == null || libro.equals(copia.getLibro()))
                && (rack == null || rack.equals(copia.getRack()))
                && (!checkReferencia.isSelected() || copia.isReferencia());
    }

    @FXML
    private void limpiarFiltros() {
        // Limpia los filtros aplicados
        cmbTipo.setValue(null);
        cmbEstado.setValue(null);
        txtPrecio.clear();
        cmbLibro.setValue(null);
        cmbRack.setValue(null);
        filtrar(); // Reaplica los filtros sin ningún criterio para mostrar todos los elementos
    }

    @FXML
    private void buscarLibro() {
        // Abre un selector de libro para elegir uno
        Libro libro = Selector.seleccionarLibro(cmbLibro.getValue());
        if (libro != null) {
            cmbLibro.setValue(libro); // Establece el libro seleccionado en el combo box
        }
    }

    @FXML
    private void verificarReferencias() {
        // Verifica las referencias de los libros y muestra un mensaje con el resultado
        try{
            String respuesta = servicio.verificarReferencias(libros);
            Alerta.mostrarMensaje(false, "Info", respuesta); // Muestra la respuesta de la verificación
        } catch (Exception e) {
            Alerta.mostrarMensaje(true, "Error", e.getMessage()); // Muestra un mensaje de error si ocurre una excepción
        }
    }
}
