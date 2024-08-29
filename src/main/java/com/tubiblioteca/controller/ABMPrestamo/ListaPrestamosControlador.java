package com.tubiblioteca.controller.ABMPrestamo;

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
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Prestamo;
import com.tubiblioteca.security.SesionManager;
import com.tubiblioteca.service.CopiaLibro.CopiaLibroServicio;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.service.Prestamo.PrestamoServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Selector;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ListaPrestamosControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Prestamo, LocalDate> colPrestamo;
    @FXML
    private TableColumn<Prestamo, LocalDate> colDevolucion;
    @FXML
    private TableColumn<Prestamo, Miembro> colMiembro;
    @FXML
    private TableColumn<Prestamo, CopiaLibro> colCopia;
    @FXML
    private TableColumn<Prestamo, Double> colMulta;

    // Tabla de miembros
    @FXML
    private TableView<Prestamo> tblPrestamos;

    // Filtros adicionales
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

    // Elementos que se le va a ocultar al usuario normal
    @FXML
    private Label lblFiltroMiembro;
    @FXML
    private Button btnBuscarMiembro;
    @FXML
    private Button btnBuscarCopia;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnConfirmarDevolucion;

    // Listas utilizadas
    private final ObservableList<Prestamo> prestamos = FXCollections.observableArrayList();
    private final ObservableList<Prestamo> filtrados = FXCollections.observableArrayList();
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<CopiaLibro> copias = FXCollections.observableArrayList();

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaPrestamosControlador.class);

    private PrestamoServicio servicio;
    private Pair<FormularioPrestamoControlador, Parent> formulario;
    private MiembroServicio servicioMiembro;
    private CopiaLibroServicio servicioCopia;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
        inicializarFiltros();
        if (SesionManager.esUsuario()) {
            ControlUI.desactivarControl(btnAgregar, btnModificar, btnEliminar, btnConfirmarDevolucion);
        }
    }

    private void inicializarTabla() {
        try {
            var repositorio = AppConfig.getRepositorio();
            servicio = new PrestamoServicio(repositorio);
            servicioMiembro = new MiembroServicio(repositorio);
            servicioCopia = new CopiaLibroServicio(repositorio);

            colPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
            colDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
            ControlUI.configurarCeldaFecha(colPrestamo, colDevolucion);
            if (SesionManager.esUsuario()) {
                tblPrestamos.getColumns().remove(colMiembro);
            } else {
                colMiembro.setCellValueFactory(new PropertyValueFactory<>("miembro"));
            }
            colCopia.setCellValueFactory(new PropertyValueFactory<>("copiaLibro"));
            colMulta.setCellValueFactory(new PropertyValueFactory<>("multa"));

            prestamos.clear();
            filtrados.clear();
            List<Prestamo> todosLosPrestamos = new ArrayList<>(servicio.buscarTodos());
            if (SesionManager.esUsuario()) {
                for (Prestamo prestamo : todosLosPrestamos) {
                    if (prestamo.getMiembro().equals(SesionManager.getMiembro())) {
                        prestamos.add(prestamo);
                    }
                }
            } else {
                prestamos.addAll(todosLosPrestamos);
            }
            filtrados.addAll(prestamos);
            tblPrestamos.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de prestamos: ", e);
        }
    }

    private void inicializarFiltros() {
        // Limpiamos las listas
        miembros.clear();
        copias.clear();

        if (SesionManager.esUsuario()) {
            // Desactivar controles para el usuario
            ControlUI.desactivarControl(cmbMiembro, lblFiltroMiembro, btnBuscarMiembro, btnBuscarCopia);
            // Cargar las copias asociadas a los préstamos del usuario
            prestamos.forEach(prestamo -> copias.add(prestamo.getCopiaLibro()));
            // Ajustar el ancho del combo de copias
            cmbCopia.setPrefWidth(200);
        } else {
            // Cargar todos los miembros y copias para el administrador
            miembros.addAll(servicioMiembro.buscarTodos());
            copias.addAll(servicioCopia.buscarTodos());

            // Establecer los ítems en los combos
            cmbMiembro.setItems(miembros);
        }

        // Establecer los ítems en el combo de copias
        cmbCopia.setItems(copias);

        // Configurar datepickers
        ControlUI.configurarDatePicker(dtpPrestamo, dtpDevolucion);
    }

    @FXML
    private void modificar() {
        Prestamo prestamo = tblPrestamos.getSelectionModel().getSelectedItem();

        if (prestamo == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un préstamo!");
        } else {
            try {
                abrirFormulario(prestamo);
                if (prestamo != null && quitarFiltro(prestamo)) {
                    filtrados.remove(prestamo);
                }
                tblPrestamos.refresh();
            } catch (Exception e) {
                log.error("Error al modificar el préstamo: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            List<Prestamo> nuevosPrestamos = abrirFormulario(null);
            if (nuevosPrestamos != null) {
                for (Prestamo prestamo : nuevosPrestamos) {
                    prestamos.add(prestamo);
                    if (aplicarFiltro(prestamo)) {
                        filtrados.add(prestamo);
                    }
                }
                tblPrestamos.refresh();
            }
        } catch (Exception e) {
            log.error("Error al agregar un préstamo: ", e);
        }
    }

    @FXML
    private void eliminar() {
        Prestamo prestamo = tblPrestamos.getSelectionModel().getSelectedItem();

        if (prestamo == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un préstamo!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el préstamo?")) {
            try {
                servicio.validarYBorrar(prestamo);
                prestamos.remove(prestamo);
                filtrados.remove(prestamo);
                Alerta.mostrarMensaje(false, "Info", "Préstamo eliminado correctamente!");
                tblPrestamos.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar el préstamo.");
                Alerta.mostrarMensaje(true, "Error", "No se pudo eliminar el préstamo.\n" + e.getMessage());
            }
        }
    }

    @FXML
    private void confirmarDevolucion() {
        Prestamo prestamo = tblPrestamos.getSelectionModel().getSelectedItem();
        try {
            servicio.cerrarPrestamo(prestamo);
            Alerta.mostrarMensaje(false, "Info", "La devolución del préstamo se ha registrado correctamente!");
            tblPrestamos.refresh();
        } catch (Exception e) {
            log.error("Error al confirmar la devolución del préstamo.");
            Alerta.mostrarMensaje(true, "Error",
                    "No se pudo confirmar la devolución del prestamo.\n" + e.getMessage());
        }
    }

    private List<Prestamo> abrirFormulario(Prestamo prestamoInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioPrestamo.getRutaFxml());
        FormularioPrestamoControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (prestamoInicial != null) {
            controladorFormulario.setPrestamoInicial(prestamoInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioPrestamo);
        return controladorFormulario.getPrestamos();
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        prestamos.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblPrestamos.refresh();
    }

    private boolean aplicarFiltro(Prestamo prestamo) {
        LocalDate fechaPrestamo = dtpPrestamo.getValue();
        LocalDate fechaDevolucion = dtpDevolucion.getValue();
        String multa = txtMulta.getText().trim().toLowerCase();
        Miembro miembro = cmbMiembro.getValue();
        CopiaLibro copia = cmbCopia.getValue();
        return (fechaPrestamo == null || prestamo.getFechaPrestamo().equals(fechaPrestamo))
                && (fechaDevolucion == null
                        || (prestamo.getFechaDevolucion() != null
                                && prestamo.getFechaDevolucion().equals(fechaDevolucion)))
                && (multa == null || String.valueOf(prestamo.getMulta()).toLowerCase().startsWith(multa))
                && (SesionManager.esUsuario() || (miembro == null || miembro.equals(prestamo.getMiembro())))
                && (copia == null || copia.equals(prestamo.getCopiaLibro()));
    }

    private boolean quitarFiltro(Prestamo prestamo) {
        LocalDate fechaPrestamo = dtpPrestamo.getValue();
        LocalDate fechaDevolucion = dtpDevolucion.getValue();
        String multa = txtMulta.getText().trim().toLowerCase();
        Miembro miembro = cmbMiembro.getValue();
        CopiaLibro copia = cmbCopia.getValue();
        return (fechaPrestamo != null && !prestamo.getFechaPrestamo().equals(fechaPrestamo))
                || (fechaDevolucion != null
                        && (prestamo.getFechaDevolucion() == null
                                || !prestamo.getFechaDevolucion().equals(fechaDevolucion)))
                || (multa != null && !String.valueOf(prestamo.getMulta()).toLowerCase().startsWith(multa))
                || (!SesionManager.esUsuario() && (miembro != null && !miembro.equals(prestamo.getMiembro())))
                || (copia != null && !copia.equals(prestamo.getCopiaLibro()));
    }

    @FXML
    private void limpiarFiltros() {
        dtpPrestamo.setValue(null);
        dtpDevolucion.setValue(null);
        cmbMiembro.setValue(null);
        cmbCopia.setValue(null);
        txtMulta.clear();
        filtrar();
    }

    @FXML
    private void buscarCopia() {
        CopiaLibro copia = Selector.seleccionarCopiaLibro(cmbCopia.getValue());
        if (copia != null) {
            cmbCopia.setValue(copia);
        }
    }

    @FXML
    private void buscarMiembro() {
        Miembro miembro = Selector.seleccionarMiembro(cmbMiembro.getValue());
        if (miembro != null) {
            cmbMiembro.setValue(miembro);
        }
    }

}