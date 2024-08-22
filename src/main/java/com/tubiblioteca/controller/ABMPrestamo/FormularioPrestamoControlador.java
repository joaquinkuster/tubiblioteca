package com.tubiblioteca.controller.ABMPrestamo;

import org.controlsfx.control.SearchableComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Selector;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class FormularioPrestamoControlador implements Initializable {

    @FXML
    private DatePicker dtpPrestamo;
    @FXML
    private DatePicker dtpDevolucion;
    @FXML
    private SearchableComboBox<Miembro> cmbMiembro;
    @FXML
    private SearchableComboBox<CopiaLibro> cmbCopia;
    @FXML
    private TextField txtMulta;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<CopiaLibro> copias = FXCollections.observableArrayList();

    private final Logger log = LoggerFactory.getLogger(FormularioPrestamoControlador.class);

    private Prestamo prestamoInicial;
    private ObservableList<Prestamo> nuevosPrestamos = FXCollections.observableArrayList();
    private PrestamoServicio servicio;
    private MiembroServicio servicioMiembro;
    private CopiaLibroServicio servicioCopia;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var repositorio = AppConfig.getRepositorio();
        servicio = new PrestamoServicio(repositorio);
        servicioMiembro = new MiembroServicio(repositorio);
        servicioCopia = new CopiaLibroServicio(repositorio);

        ControlUI.configurarDatePicker(dtpPrestamo);
        ControlUI.configurarDatePicker(dtpDevolucion);
        dtpPrestamo.setValue(LocalDate.now());

        inicializarCombosFormulario();

        prestamoInicial = null;
        nuevosPrestamos.clear();

        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
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
    }

    @FXML
    private void guardar() {
        try {

            LocalDate fechaPrestamo = dtpPrestamo.getValue();
            LocalDate fechaDevolucion = dtpDevolucion.getValue();
            Miembro miembro = cmbMiembro.getValue();
            CopiaLibro copia = cmbCopia.getValue();
            String multa = txtMulta.getText().trim();

            if (prestamoInicial == null) {
                nuevosPrestamos.add(servicio.validarEInsertar(fechaPrestamo, miembro, copia));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el préstamo correctamente!");
            } else {
                servicio.validarYModificar(prestamoInicial, fechaPrestamo, fechaDevolucion, miembro, copia, multa);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el préstamo correctamente!");
                StageManager.cerrarModal(Vista.FormularioPrestamo);
            }
        } catch (Exception e) {
            log.error("Error al guardar el prestamo.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el prestamo.\n" + e.getMessage());
        }
    }

    @FXML
    public void calcularMulta() {
        LocalDate fechaPrestamo = dtpPrestamo.getValue();
        LocalDate fechaDevolucion = dtpDevolucion.getValue();

        txtMulta.setText("0.0");

        // Verificar si las fechas de préstamo y devolución están seleccionadas
        if (fechaPrestamo == null || fechaDevolucion == null) {
            return;
        }

        // Comprobar si la devolución es después o el mismo día del préstamo
        if (!fechaDevolucion.isBefore(fechaPrestamo)) {
            LocalDate fechaVencimiento = fechaPrestamo.plusDays(10);
            long diasRetraso = ChronoUnit.DAYS.between(fechaVencimiento, fechaDevolucion);

            // Si hay días de retraso, calcular la multa
            if (diasRetraso > 0) {
                CopiaLibro copia = cmbCopia.getValue();

                // Verificar si la copia del libro está seleccionada
                if (copia != null) {
                    double multa = copia.getPrecio() * diasRetraso;
                    txtMulta.setText(String.format("%.2f", multa).replace(',', '.'));
                }
            }
        }
    }

    @FXML
    private void buscarCopia() {
    }

    @FXML
    private void buscarMiembro() {
        Miembro miembro = Selector.seleccionarMiembro(cmbMiembro.getValue());
        if (miembro != null) {
            cmbMiembro.setValue(miembro);
        }
    }

    private void autocompletar() {
        dtpPrestamo.setValue(prestamoInicial.getFechaPrestamo());
        dtpDevolucion.setValue(prestamoInicial.getFechaDevolucion());
        cmbMiembro.setValue(prestamoInicial.getMiembro());
        cmbCopia.setValue(prestamoInicial.getCopiaLibro());
        txtMulta.setText(String.valueOf(prestamoInicial.getMulta()));
    }

    public void setPrestamoInicial(Prestamo prestamo) {
        if (prestamo != null) {
            this.prestamoInicial = prestamo;
            autocompletar();
            dtpDevolucion.setDisable(false);
            txtMulta.setDisable(false);
            btnNuevo.setDisable(true);
        }
    }

    public List<Prestamo> getPrestamos() {
        return nuevosPrestamos;
    }
}
