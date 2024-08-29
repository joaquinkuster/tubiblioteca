package com.tubiblioteca.controller.ABMCopiaLibro;

// Importaciones necesarias para el controlador y sus dependencias
import org.controlsfx.control.SearchableComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.controller.ABMLibro.FormularioLibroControlador;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Selector;
import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.EstadoCopiaLibro;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.model.Rack;
import com.tubiblioteca.model.TipoCopiaLibro;
import com.tubiblioteca.service.CopiaLibro.CopiaLibroServicio;
import com.tubiblioteca.service.Libro.LibroServicio;
import com.tubiblioteca.service.Rack.RackServicio;
import com.tubiblioteca.view.Vista;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FormularioCopiaLibroControlador implements Initializable {

    // Elementos de la interfaz de usuario
    @FXML
    private SearchableComboBox<TipoCopiaLibro> cmbTipo;  // ComboBox para seleccionar el tipo de copia de libro
    @FXML
    private TextField txtPrecio;  // Campo de texto para el precio
    @FXML
    private SearchableComboBox<Libro> cmbLibro;  // ComboBox para seleccionar el libro
    @FXML
    private SearchableComboBox<Rack> cmbRack;  // ComboBox para seleccionar el rack
    @FXML
    private Spinner<Integer> spinCantidad;  // Spinner para seleccionar la cantidad
    @FXML 
    private CheckBox checkReferencia;  // CheckBox para indicar si es una referencia
    @FXML
    private CheckBox checkPerdida;  // CheckBox para indicar si está perdida

    @FXML
    private Button btnNuevo;  // Botón para crear una nueva copia
    @FXML
    private Button btnGuardar;  // Botón para guardar la copia

    // Listas observables para los elementos en los ComboBox
    private final ObservableList<TipoCopiaLibro> tipos = FXCollections.observableArrayList();
    private final ObservableList<Libro> libros = FXCollections.observableArrayList();
    private final ObservableList<Rack> racks = FXCollections.observableArrayList();

    // Logger para gestionar información y errores
    private final Logger log = LoggerFactory.getLogger(FormularioLibroControlador.class);

    // Variables para manejar la copia inicial y nuevas copias
    private CopiaLibro copiaInicial;
    private ObservableList<CopiaLibro> nuevasCopias = FXCollections.observableArrayList();

    // Servicios para manejar las operaciones de copia, libro y rack
    private CopiaLibroServicio servicio;
    private LibroServicio servicioLibro;
    private RackServicio servicioRack;

    // Método llamado al inicializar el controlador
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa los servicios con el repositorio obtenido de la configuración de la aplicación
        var repositorio = AppConfig.getRepositorio();
        servicio = new CopiaLibroServicio(repositorio);
        servicioLibro = new LibroServicio(repositorio);
        servicioRack = new RackServicio(repositorio);

        // Configura el spinner de cantidad
        ControlUI.configurarSpinnerCantidad(spinCantidad);

        // Inicializa los ComboBox con datos
        inicializarCombosFormulario();

        // Inicializa la copia inicial como nula y limpia la lista de nuevas copias
        copiaInicial = null;
        nuevasCopias.clear();

        // Configura el atajo de teclado para el botón guardar
        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    // Método para inicializar los ComboBox con datos
    private void inicializarCombosFormulario() {
        // Inicializa y configura el ComboBox de tipos
        tipos.clear();
        tipos.addAll(TipoCopiaLibro.values());
        cmbTipo.setItems(tipos);

        // Inicializa y configura el ComboBox de libros
        libros.clear();
        libros.addAll(servicioLibro.buscarTodos());
        cmbLibro.setItems(libros);

        // Inicializa y configura el ComboBox de racks
        racks.clear();
        racks.addAll(servicioRack.buscarTodos());
        cmbRack.setItems(racks);
    }

    // Método llamado cuando se presiona el botón "Nuevo"
    @FXML
    private void nuevo() {
        // Limpia los campos del formulario
        cmbTipo.setValue(null);
        txtPrecio.clear();
        cmbLibro.setValue(null);
        cmbRack.setValue(null);
        spinCantidad.getValueFactory().setValue(1);
        checkReferencia.setSelected(false);
    }

    // Método llamado cuando se presiona el botón "Guardar"
    @FXML
    private void guardar() {
        try {
            // Obtiene los valores de los campos del formulario
            TipoCopiaLibro tipo = cmbTipo.getValue();
            String precio = txtPrecio.getText().trim();
            Libro libro = cmbLibro.getValue();
            Rack rack = cmbRack.getValue();
            boolean referencia = checkReferencia.isSelected();
            boolean estaPerdida = checkPerdida.isSelected();
            int cantidad = spinCantidad.getValue();

            if (copiaInicial == null) {
                // Si no hay una copia inicial, se crean nuevas copias
                for (int i = 0; i < cantidad; i++) {
                    nuevasCopias.add(servicio.validarEInsertar(tipo, precio, libro, rack, referencia));
                }
                Alerta.mostrarMensaje(false, "Info", "Se han agregado las copias correctamente!");
            } else {
                // Si hay una copia inicial, se modifica la copia existente
                servicio.validarYModificar(copiaInicial, tipo, precio, libro, rack, referencia, estaPerdida);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado la copia correctamente!");
                StageManager.cerrarModal(Vista.FormularioCopiaLibro);
            }
        } catch (Exception e) {
            // Registra un error si ocurre un problema al guardar la copia
            log.error("Error al guardar la copia.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar la copia.\n" + e.getMessage());
        }
    }

    // Método para autocompletar los campos del formulario con los valores de la copia inicial
    private void autocompletar() {
        if (copiaInicial != null) {
            cmbTipo.setValue(copiaInicial.getTipo());
            txtPrecio.setText(String.valueOf(copiaInicial.getPrecio()));
            cmbLibro.setValue(copiaInicial.getLibro());
            cmbRack.setValue(copiaInicial.getRack());
            checkReferencia.setSelected(copiaInicial.isReferencia());
            checkPerdida.setSelected(copiaInicial.getEstado().equals(EstadoCopiaLibro.Perdida));
        }
    }

    // Método para establecer una copia inicial y configurar el formulario en consecuencia
    public void setCopiaInicial(CopiaLibro copia) {
        if (copia != null) {
            this.copiaInicial = copia;
            autocompletar();
            spinCantidad.setDisable(true);
            checkPerdida.setDisable(false);
            btnNuevo.setDisable(true);
        }
    }

    // Método para obtener la lista de nuevas copias
    public List<CopiaLibro> getCopias() {
        return nuevasCopias;
    }

    // Método llamado para buscar un libro
    @FXML
    private void buscarLibro() {
        Libro libro = Selector.seleccionarLibro(cmbLibro.getValue());
        if (libro != null) {
            cmbLibro.setValue(libro);
        }
    }
}
