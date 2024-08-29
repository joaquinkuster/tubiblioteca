package com.tubiblioteca.controller.Auditoria;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.controlsfx.control.CheckComboBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Auditoria;
import com.tubiblioteca.service.Auditoria.AuditoriaServicio;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;

public class ListaAuditoriaControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Auditoria, LocalDateTime> colFecha;
    @FXML
    private TableColumn<Auditoria, String> colUsuario;
    @FXML
    private TableColumn<Auditoria, String> colAccion;
    @FXML
    private TableColumn<Auditoria, String> colTablaAfectada;
    @FXML
    private TableColumn<Auditoria, String> colDescripcion;

    // Tabla de auditoriaW
    @FXML
    private TableView<Auditoria> tblAuditoria;

    // Filtros adicionales
    @FXML
    private DatePicker dtpAuditoria;
    @FXML
    private CheckComboBox<Miembro> cmbUsuario;
    @FXML
    private CheckComboBox<String> cmbAccion;
    @FXML
    private CheckComboBox<String> cmbTablaAfectada;
    @FXML
    private TextField txtDatoAfectado;

    // Listas utilizadas
    private final ObservableList<Auditoria> auditorias = FXCollections.observableArrayList();
    private final ObservableList<Auditoria> filtrados = FXCollections.observableArrayList();

    private AuditoriaServicio servicio;
    private MiembroServicio miembroServicio;

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaAuditoriaControlador.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
        inicializarFiltros();
    }

    private void inicializarTabla() {
        try {
            var repositorio = AppConfig.getRepositorio();
            servicio = new AuditoriaServicio(repositorio);
            miembroServicio = new MiembroServicio(repositorio);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
            colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
            colFecha.setCellFactory(new Callback<>() {
                @Override
                public TableCell<Auditoria, LocalDateTime> call(TableColumn<Auditoria, LocalDateTime> param) {
                    return new TableCell<>() {
                        @Override
                        protected void updateItem(LocalDateTime item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                            } else {
                                setText(item.format(formatter));
                            }
                        }
                    };
                }
            });

            colUsuario.setCellValueFactory(new PropertyValueFactory<>("miembro"));
            colAccion.setCellValueFactory(new PropertyValueFactory<>("accion"));
            colTablaAfectada.setCellValueFactory(new PropertyValueFactory<>("tablaAfectada"));
            inicializarColumnaDescripcion();

            // Limpiar y cargar los datos de auditoría
            auditorias.clear();
            filtrados.clear();
            auditorias.addAll(servicio.buscarTodos());
            filtrados.addAll(auditorias);

            // Establecer los datos en la tabla
            tblAuditoria.setItems(filtrados);


        } catch (Exception e) {
            log.error("Error al inicializar la lista de auditoria: ", e);
        }
    }

    private void inicializarColumnaDescripcion() {
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        
        colDescripcion.setCellFactory(new Callback<TableColumn<Auditoria, String>, TableCell<Auditoria, String>>() {
            @Override
            public TableCell<Auditoria, String> call(TableColumn<Auditoria, String> param) {
                return new TableCell<Auditoria, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            setText(ControlUI.limitar(item, 30));
                            Tooltip tooltip = new Tooltip(item);
                            setTooltip(tooltip);
                        }
                    }
                };
            }
        });
    }

    private void inicializarFiltros() {

        List<Miembro> miembros = miembroServicio.buscarTodos();
        cmbUsuario.getItems().addAll(miembros);
        cmbTablaAfectada.getItems().addAll("Autor", "Categoria", "Copias de libros", "Editorial", "Idioma", "Libro",
                "Miembro", "Prestamo", "Rack");
        cmbAccion.getItems().addAll("Alta", "Baja", "Modificacion");
        configurarListenerCombos(cmbAccion);
        configurarListenerCombos(cmbTablaAfectada);
        configurarListenerCombos(cmbUsuario);

        // Configurar datepickers
        ControlUI.configurarDatePicker(dtpAuditoria);
    }

    private <T> void configurarListenerCombos(CheckComboBox<T> cmb) {
        // Listener para detectar cambios en los autores seleccionados
        cmb.getCheckModel().getCheckedItems().addListener(new ListChangeListener<T>() {
            @Override
            public void onChanged(Change<? extends T> change) {
                while (change.next()) {
                    if (change.wasAdded() || change.wasRemoved()) {
                        List<T> seleccionados = new ArrayList<>(cmb.getCheckModel().getCheckedItems());
                        filtrar();
                    }
                }
            }
        });
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        auditorias.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblAuditoria.refresh();
    }

    private boolean aplicarFiltro(Auditoria auditoria) {
        LocalDate fechaHora = dtpAuditoria.getValue();
        boolean coincideSoloFecha = fechaHora == null || auditoria.getFechaHora().toLocalDate().equals(fechaHora);
        String datoAfectado = txtDatoAfectado.getText().trim().toLowerCase();
        return coincideSoloFecha
                && (filtradoIndividualCheckCombo(auditoria.getMiembro().toString(), cmbUsuario))
                && (filtradoIndividualCheckCombo(auditoria.getAccion().toString(), cmbAccion))
                && (filtradoIndividualCheckCombo(auditoria.getTablaAfectada(), cmbTablaAfectada))
                && (datoAfectado == null
                        || String.valueOf(auditoria.getDescripcion()).toLowerCase().startsWith(datoAfectado));
    }

    private <T> boolean filtradoIndividualCheckCombo(String dato, CheckComboBox<T> cmb) {
        List<T> seleccionados = new ArrayList<>(cmb.getCheckModel().getCheckedItems());

        if (seleccionados == null || seleccionados.isEmpty()) {
            return true;
        } else {
            for (T seleccionado : seleccionados) {
                if (seleccionado instanceof String) {
                    // Comparación para String
                    if (dato.contains((String) seleccionado)) {
                        return true;
                    }
                } else if (seleccionado instanceof Miembro) {
                    // Comparación para Miembro
                    Miembro miembro = (Miembro) seleccionado;
                    if (dato.contains(miembro.getNombre())) {
                        return true;
                    }
                } else {
                    System.out.println("Tipo no soportado: " + seleccionado.getClass().getName());
                    return false;
                }
            }
        }

        return false;
    }

    @FXML
    private void limpiarFiltros() {
        dtpAuditoria.setValue(null);
        cmbUsuario.getCheckModel().clearChecks();
        cmbAccion.getCheckModel().clearChecks();
        cmbTablaAfectada.getCheckModel().clearChecks();
        txtDatoAfectado.clear();
        filtrar();
    }
}