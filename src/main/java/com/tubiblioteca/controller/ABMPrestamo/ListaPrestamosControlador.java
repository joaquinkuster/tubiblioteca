package com.tubiblioteca.controller.ABMPrestamo;

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
import com.tubiblioteca.controller.ABMMiembro.FormularioMiembroControlador;
import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Prestamo;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.service.Prestamo.PrestamoServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.Fecha;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
    private ComboBox<Miembro> cmbMiembro;
    @FXML
    private ComboBox<CopiaLibro> cmbCopia;
    @FXML
    private TextField txtMulta;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
        inicializarFiltros();
    }

    private void inicializarTabla() {
        try {
            servicio = new PrestamoServicio(AppConfig.getRepositorio());

            colPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
            colDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
            colMiembro.setCellValueFactory(new PropertyValueFactory<>("miembro"));
            colCopia.setCellValueFactory(new PropertyValueFactory<>("copiaLibro"));
            colMulta.setCellValueFactory(new PropertyValueFactory<>("multa"));

            configurarCeldaFecha(colPrestamo);
            configurarCeldaFecha(colDevolucion);

            prestamos.clear();
            filtrados.clear();
            prestamos.addAll(servicio.buscarTodos());
            filtrados.addAll(prestamos);
            tblPrestamos.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de prestamos: ", e);
        }
    }

    private void configurarCeldaFecha(TableColumn<Prestamo, LocalDate> columna) {
        // Establecemos una fábrica de celdas para la columna
        columna.setCellFactory(column -> {
            // Devolvemos una nueva celda para la visualizacion de datos
            return new TableCell<>() {
                // Sobreescribimos el metodo para personalizar el comportamiento de
                // actualizacion de celda
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    // Llamamos a la clase padre para cualquier inicializacion necesaria
                    super.updateItem(fecha, empty);
                    // Verificamos si la fecha es nula
                    if (fecha == null || empty) {
                        setText(!empty ? "--" : null);
                    } else {
                        // Si es diferente de nula, formateamos la fecha antes de mostrarla en la celda
                        setText(Fecha.formatearFecha(fecha));
                    }
                }
            };
        });
    }

    private void inicializarFiltros() {
        miembros.clear();
        miembros.addAll(servicioMiembro.buscarTodos());
        cmbMiembro.setItems(miembros);

        copias.clear();
        copias.addAll();
        cmbCopia.setItems(copias);
    }

    @FXML
    private void modificar() {
        Prestamo prestamo = tblPrestamos.getSelectionModel().getSelectedItem();

        if (prestamo == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un préstamo!");
        } else {
            try {
                prestamo = abrirFormulario(prestamo);
                if (prestamo != null && quitarFiltro(prestamo)) {
                    filtrados.remove(prestamo);
                }
                tblPrestamos.refresh();
            } catch (Exception e) {
                log.error("Error al modificar el prestamo: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            Prestamo prestamo = abrirFormulario(null);
            if (prestamo != null) {
                prestamos.add(prestamo);
                if (aplicarFiltro(prestamo)) {
                    filtrados.add(prestamo);
                    tblPrestamos.refresh();
                }
            }
        } catch (Exception e) {
            log.error("Error al agregar un prestamo: ", e);
        }
    }

    @FXML
    private void eliminar() {
        Prestamo prestamo = tblPrestamos.getSelectionModel().getSelectedItem();

        if (prestamo == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un préstamo!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el préstamo?")) {
            try {
                servicio.borrar(prestamo);
                prestamos.remove(prestamo);
                filtrados.remove(prestamo);
                Alerta.mostrarMensaje(false, "Info", "Préstamo eliminado correctamente!");
                tblPrestamos.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar el miembro: ", e);
                Alerta.mostrarMensaje(true, "Error",
                        "No se pudo eliminar el préstamo. Puede estar vinculado a otros registros.");
            }
        }
    }
    
    private Prestamo abrirFormulario(Prestamo prestamoInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioPrestamo.getRutaFxml());
        FormularioPrestamoControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (prestamoInicial != null) {
            controladorFormulario.setPrestamo(prestamoInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioMiembro);
        return controladorFormulario.getMiembro();
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
                && (fechaDevolucion == null || prestamo.getFechaDevolucion().equals(fechaDevolucion))
                && (multa == null || String.valueOf(prestamo.getMulta()).toLowerCase().contains(multa))
                && (miembro == null || miembro.equals(prestamo.getMiembro()))
                && (copia == null || copia.equals(prestamo.getCopiaLibro()));
    }

    private boolean quitarFiltro(Prestamo prestamo) {
        LocalDate fechaPrestamo = dtpPrestamo.getValue();
        LocalDate fechaDevolucion = dtpDevolucion.getValue();
        String multa = txtMulta.getText().trim().toLowerCase();
        Miembro miembro = cmbMiembro.getValue();
        CopiaLibro copia = cmbCopia.getValue();
        return (fechaPrestamo != null && !prestamo.getFechaPrestamo().equals(fechaPrestamo))
                || (fechaDevolucion != null && !prestamo.getFechaDevolucion().equals(fechaDevolucion))
                || (multa != null && !String.valueOf(prestamo.getMulta()).toLowerCase().contains(multa))
                || (miembro != null && !miembro.equals(prestamo.getMiembro()))
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
}
