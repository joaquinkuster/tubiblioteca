package com.tubiblioteca.controller.ABMLibro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.view.Vista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FormularioPrestamoControlador implements Initializable {

    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private ComboBox<TipoMiembro> cmbTipo;
    @FXML
    private Button btnNuevo;

    private ObservableList<TipoMiembro> tipos = FXCollections.observableArrayList();
    private final Logger log = LoggerFactory.getLogger(FormularioPrestamoControlador.class);

    private Miembro miembro;
    private MiembroServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarCombosFormulario();
        miembro = null;
    }

    private void inicializarCombosFormulario() {
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
        txtDni.setDisable(false);
        btnNuevo.setDisable(false);
    }

    @FXML
    private void guardar() {
        try {
            // Creamos un nuevo miembro auxiliar
            Miembro aux = new Miembro(
                    txtDni.getText().trim(),
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    cmbTipo.getValue(),
                    txtContrasena.getText().trim()
            );

            if (miembro == null) {
                // Validamos si el DNI ya está en uso
                if (servicio.buscarPorId(aux.getDni()) == null) {
                    miembro = aux;
                    servicio.insertar(aux);
                    Alerta.mostrarMensaje(false, "Info", "Se ha agregado el miembro de la biblioteca correctamente!");
                } else {
                    throw new IllegalArgumentException("El DNI ingresado ya está en uso. Por favor, ingrese otro DNI.");
                }
            } else {
                // Actualizamos el miembro existente
                miembro.setNombre(aux.getNombre());
                miembro.setApellido(aux.getApellido());
                miembro.setTipo(aux.getTipo());
                miembro.setClave(aux.getClave());

                servicio.modificar(miembro);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el miembro de la biblioteca correctamente!");
            }

            StageManager.cerrarModal(Vista.FormularioMiembro);
        } catch (Exception e) {
            log.error("Error al guardar el miembro.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el miembro de la biblioteca. " + e.getMessage());
        }
    }

    private void autocompletar() {
        txtDni.setText(miembro.getDni());
        txtNombre.setText(miembro.getNombre());
        txtApellido.setText(miembro.getApellido());
        cmbTipo.setValue(miembro.getTipo());
        txtContrasena.setText(miembro.getClave());
    }

    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;
        if (miembro != null) {
            autocompletar();
            txtDni.setDisable(true);
            btnNuevo.setDisable(true);
        }
    }

    public Miembro getMiembro() {
        return miembro;
    }

    public void setServicio(MiembroServicio servicio) {
        this.servicio = servicio;
    }
}
