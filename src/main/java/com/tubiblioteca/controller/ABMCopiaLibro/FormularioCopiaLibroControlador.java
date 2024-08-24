package com.tubiblioteca.controller.ABMCopiaLibro;

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

    @FXML
    private SearchableComboBox<TipoCopiaLibro> cmbTipo;
    @FXML
    private TextField txtPrecio;
    @FXML
    private SearchableComboBox<Libro> cmbLibro;
    @FXML
    private SearchableComboBox<Rack> cmbRack;
    @FXML
    private Spinner<Integer> spinCantidad;
    @FXML 
    private CheckBox checkReferencia;
    @FXML
    private CheckBox checkPerdida;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final ObservableList<TipoCopiaLibro> tipos = FXCollections.observableArrayList();
    private final ObservableList<Libro> libros = FXCollections.observableArrayList();
    private final ObservableList<Rack> racks = FXCollections.observableArrayList();

    private final Logger log = LoggerFactory.getLogger(FormularioLibroControlador.class);

    private CopiaLibro copiaInicial;
    private ObservableList<CopiaLibro> nuevasCopias = FXCollections.observableArrayList();;

    private CopiaLibroServicio servicio;
    private LibroServicio servicioLibro;
    private RackServicio servicioRack;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var repositorio = AppConfig.getRepositorio();
        servicio = new CopiaLibroServicio(repositorio);
        servicioLibro = new LibroServicio(repositorio);
        servicioRack = new RackServicio(repositorio);

        ControlUI.configurarSpinnerCantidad(spinCantidad);

        inicializarCombosFormulario();

        copiaInicial = null;
        nuevasCopias.clear();

        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    private void inicializarCombosFormulario() {
        tipos.clear();
        tipos.addAll(TipoCopiaLibro.values());
        cmbTipo.setItems(tipos);

        libros.clear();
        libros.addAll(servicioLibro.buscarTodos());
        cmbLibro.setItems(libros);

        racks.clear();
        racks.addAll(servicioRack.buscarTodos());
        cmbRack.setItems(racks);
    }

    @FXML
    private void nuevo() {
        cmbTipo.setValue(null);
        txtPrecio.clear();
        cmbLibro.setValue(null);
        cmbRack.setValue(null);
        spinCantidad.getValueFactory().setValue(1);
        checkReferencia.setSelected(false);
    }

    @FXML
    private void guardar() {
        try {
            
            TipoCopiaLibro tipo = cmbTipo.getValue();
            String precio = txtPrecio.getText().trim();
            Libro libro =cmbLibro.getValue();
            Rack rack = cmbRack.getValue();
            boolean referencia = checkReferencia.isSelected();
            boolean estaPerdida = checkPerdida.isSelected();

            int cantidad = spinCantidad.getValue();

            if (copiaInicial == null) {
                for (int i = 0; i < cantidad; i++) {
                    nuevasCopias.add(servicio.validarEInsertar(tipo, precio, libro, rack, referencia));
                }
                Alerta.mostrarMensaje(false, "Info", "Se han agregados las copias correctamente!");
            } else {
                servicio.validarYModificar(copiaInicial, tipo, precio, libro, rack, referencia, estaPerdida);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado la copia correctamente!");
                StageManager.cerrarModal(Vista.FormularioCopiaLibro);
            }
        } catch (Exception e) {
            log.error("Error al guardar la copia.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar la copia.\n" + e.getMessage());
        }
    }

    private void autocompletar() {
        cmbTipo.setValue(copiaInicial.getTipo());
        txtPrecio.setText(String.valueOf(copiaInicial.getPrecio()));
        cmbLibro.setValue(copiaInicial.getLibro());
        cmbRack.setValue(copiaInicial.getRack());
        checkReferencia.setSelected(copiaInicial.isReferencia());
        checkPerdida.setSelected(copiaInicial.getEstado().equals(EstadoCopiaLibro.Perdida));
    }

    public void setCopiaInicial(CopiaLibro copia) {
        if (copia != null) {
            this.copiaInicial = copia;
            autocompletar();
            spinCantidad.setDisable(true);
            checkPerdida.setDisable(false);
            btnNuevo.setDisable(true);
        }
    }

    public List<CopiaLibro> getCopias() {
        return nuevasCopias;
    }

    @FXML
    private void buscarLibro() {
        Libro libro = Selector.seleccionarLibro(cmbLibro.getValue());
        if (libro != null) {
            cmbLibro.setValue(libro);
        }
    }
}
