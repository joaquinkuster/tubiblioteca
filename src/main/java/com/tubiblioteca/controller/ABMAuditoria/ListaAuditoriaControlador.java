package com.tubiblioteca.controller.ABMAuditoria;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Auditoria;
import com.tubiblioteca.auditoria.AuditoriaServicio;
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

    // Tabla de libros
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

            colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
            colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
            colAccion.setCellValueFactory(new PropertyValueFactory<>("accion"));
            colTablaAfectada.setCellValueFactory(new PropertyValueFactory<>("tablaAfectada"));
            colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

            // Limpiar y cargar los datos de auditoría
            ObservableList<Auditoria> auditorias = FXCollections.observableArrayList();
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

    private void inicializarFiltros() {

         List<Miembro> miembros = miembroServicio.buscarTodos(); 
         System.out.println(miembros);
         cmbUsuario.getItems().addAll(miembros);

         cmbAccion.getItems().setAll("alta", "baja", "modificacion");
 
         cmbTablaAfectada.getItems().addAll("Autores", "Categorías", "Copias de libros", "Editoriales", "Idiomas", "Libros", "Miembros", "Racks" );

         configurarListenerCombos(cmbAccion);
         configurarListenerCombos(cmbTablaAfectada);
         configurarListenerCombos(cmbUsuario);
    }

    private <T>  void configurarListenerCombos(CheckComboBox<T> cmb) {
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
        String datoAfectado = txtDatoAfectado.getText().trim().toLowerCase();
        return (fechaHora == null || auditoria.getFechaHora().equals(fechaHora))//ver como hacer el mismo tipo abzo
                && (filtradoIndividualCheckCombo(auditoria.getMiembro().toString(), cmbUsuario))
                && (filtradoIndividualCheckCombo(auditoria.getAccion(), cmbAccion))
                && (filtradoIndividualCheckCombo(auditoria.getTablaAfectada(), cmbTablaAfectada))
                && (datoAfectado == null || String.valueOf(auditoria.getDatoAfectado()).toLowerCase().startsWith(datoAfectado));
    }

    private <T> boolean filtradoIndividualCheckCombo(String dato, CheckComboBox<T> cmb) {
        List<T> seleccionados = new ArrayList<>(cmb.getCheckModel().getCheckedItems());
        
        if (seleccionados == null || seleccionados.isEmpty()) {
            return true;
        } else {
            for (T seleccionado : seleccionados) {
                if (seleccionado instanceof String) {
                    // Comparación para String
                    if (!dato.contains((String) seleccionado)) {
                        return false;
                    }
                } else if (seleccionado instanceof Miembro) {
                    // Comparación para Miembro
                    Miembro miembro = (Miembro) seleccionado;
                    if (!dato.contains(miembro.getNombre())) {
                        return false;
                    }
                } else {
                    // Para otros tipos, puedes definir una lógica específica si es necesario
                    System.out.println("Tipo no soportado: " + seleccionado.getClass().getName());
                    return false;
                }
            }
        }
        
        return true;
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