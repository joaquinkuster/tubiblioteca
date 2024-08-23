package com.tubiblioteca.controller.ABMMiembro;

import org.controlsfx.control.SearchableComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.security.Contraseña;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.view.Vista;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FormularioMiembroControlador implements Initializable {

    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private SearchableComboBox<TipoMiembro> cmbTipo;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final ObservableList<TipoMiembro> tipos = FXCollections.observableArrayList();
    private final Logger log = LoggerFactory.getLogger(FormularioMiembroControlador.class);

    private Miembro miembroInicial;
    private ObservableList<Miembro> nuevosMiembros = FXCollections.observableArrayList();;
    private MiembroServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new MiembroServicio(AppConfig.getRepositorio());
        inicializarCombosFormulario();
        miembroInicial = null;
        nuevosMiembros.clear();
        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    private void inicializarCombosFormulario() {
        tipos.clear();
        tipos.addAll(TipoMiembro.values());
        cmbTipo.setItems(tipos);
    }

    @FXML
    private void nuevo() {
        txtDni.clear();
        txtNombre.clear();
        txtApellido.clear();
        cmbTipo.setValue(null);
        txtContrasena.clear();
    }

    @FXML
    private void guardar() {
        try {

            String dni = txtDni.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            TipoMiembro tipo = cmbTipo.getValue();
            String clave = txtContrasena.getText().trim();

            if (miembroInicial == null) {
                nuevosMiembros.add(servicio.validarEInsertar(dni, nombre, apellido, tipo, clave));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el miembro de la biblioteca correctamente!");
            } else {
                servicio.validarYModificar(miembroInicial, nombre, apellido, tipo, clave);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el miembro de la biblioteca correctamente!");
                StageManager.cerrarModal(Vista.FormularioMiembro);
            }
        } catch (Exception e) {
            log.error("Error al guardar el miembro.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el miembro de la biblioteca.\n" + e.getMessage());
        }
    }

    private void autocompletar() {
        txtDni.setText(miembroInicial.getDni());
        txtNombre.setText(miembroInicial.getNombre());
        txtApellido.setText(miembroInicial.getApellido());
        cmbTipo.setValue(miembroInicial.getTipo());
    }

    public void setMiembroInicial(Miembro miembro) {
        this.miembroInicial = miembro;
        if (miembro != null) {
            autocompletar();
            txtDni.setDisable(true);
            btnNuevo.setDisable(true);
        }
    }

    public List<Miembro> getMiembros() {
        return nuevosMiembros;
    }
}
