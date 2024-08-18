package com.tubiblioteca.controller.ABMPrestamo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.controller.ABMLibro.FormularioLibroControlador;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Prestamo;
import com.tubiblioteca.service.CopiaLibro.CopiaLibroServicio;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.service.Prestamo.PrestamoServicio;
import com.tubiblioteca.view.Vista;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FormularioPrestamoControlador implements Initializable {

    @FXML
    private DatePicker dtpPrestamo;
    @FXML
    private DatePicker dtpDevolucion;
    @FXML
    private ComboBox<Miembro> cmbMiembro;
    @FXML
    private ComboBox<CopiaLibro> cmbCopia;
    @FXML
    private TextField txtMulta;
    @FXML
    private Spinner<Integer> spinCantidad;

    @FXML
    private Button btnNuevo;

    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<CopiaLibro> copias = FXCollections.observableArrayList();

    private final Logger log = LoggerFactory.getLogger(FormularioLibroControlador.class);

    private List<Prestamo> prestamos;
    private PrestamoServicio servicio;
    private MiembroServicio servicioMiembro;
    private CopiaLibroServicio servicioCopia;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var repositorio = AppConfig.getRepositorio();
        servicio = new PrestamoServicio(repositorio);
        servicioMiembro = new MiembroServicio(repositorio);
        servicioCopia = new CopiaLibroServicio(repositorio);

        ControlUI.configurarSpinnerCantidad(spinCantidad);

        dtpPrestamo.setValue(LocalDate.now());

        inicializarCombosFormulario();

        prestamos.clear();
    }

    private void inicializarCombosFormulario() {
        miembros.clear();
        miembros.addAll(servicioMiembro.buscarTodos());
        cmbMiembro.setItems(miembros);

        copias.clear();
        copias.addAll(servicioCopia.buscarTodos());
        cmbCopia.setItems(copias);
    }

    @FXML
    private void nuevo() {
        dtpPrestamo.setValue(null);
        cmbMiembro.setValue(null);
        cmbCopia.setValue(null);
        spinCantidad.getValueFactory().setValue(1);
    }

    @FXML
    private void guardar() {
        try {
            // Creamos un nuevo prestamo auxiliar
            Prestamo aux = new Prestamo(
                    dtpPrestamo.getValue(),
                    cmbMiembro.getValue(),
                    cmbCopia.getValue());

            ArrayList<String> errores = new ArrayList<>();

            // Validamos si el miembro o la copia seleccionados existen
            if (servicioMiembro.buscarPorId(aux.getMiembro().getDni()) == null) {
                errores.add("El miembro de la biblioteca seleccionado no se encuentra en la base de datos.");
            }
            if (servicioCopia.buscarPorId(aux.getCopiaLibro().getId()) == null) {
                errores.add("La copia del libro seleccionada no se encuentra en la base de datos.");
            }

            if (!errores.isEmpty()) {
                throw new IllegalArgumentException(Alerta.convertirCadenaErrores(errores));
            }

            if (prestamos.getFirst() == null) {
                for (int i = 0; i < spinCantidad.getValue(); i++){
                    prestamos.add(aux);
                }
                servicio.insertar(aux);
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el préstamo correctamente!");
            } else {
                // Actualizamos el libro existente
                prestamo.setFechaPrestamo(aux.getFechaPrestamo());
                prestamo.setMiembro(aux.getMiembro());
                prestamo.setCopiaLibro(aux.getCopiaLibro());

                servicio.modificar(prestamo);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el préstamo correctamente!");
            }

            StageManager.cerrarModal(Vista.FormularioPrestamo);
        } catch (Exception e) {
            log.error("Error al guardar el prestamo.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el prestamo. " + e.getMessage());
        }
    }

    private void autocompletar() {
        dtpPrestamo.setValue(prestamos.getFirst().getFechaPrestamo());
        dtpDevolucion.setValue(prestamos.getFirst().getFechaDevolucion());
        cmbMiembro.setValue(prestamos.getFirst().getMiembro());
        cmbCopia.setValue(prestamos.getFirst().getCopiaLibro());
    }

    public void setPrestamo(Prestamo prestamo) {
        if (prestamo != null) {
            prestamos.add(prestamo);
            autocompletar();
            dtpDevolucion.setDisable(false);
            spinCantidad.setDisable(true);
            txtMulta.setDisable(false);
            btnNuevo.setDisable(true);
        }
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }
}
