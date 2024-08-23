package com.tubiblioteca.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import com.tubiblioteca.view.Vista;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class CambiarContraseñaControlador implements Initializable {

    // Campos del formulario
    @FXML
    private PasswordField txtActual;
    @FXML
    private PasswordField txtNueva;
    @FXML
    private PasswordField txtRepetirNueva;
    @FXML
    private Button btnGuardar;

    private MiembroServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new MiembroServicio(AppConfig.getRepositorio());
        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    @FXML
    private void guardar() {
        try {
            servicio.cambiarClave(txtActual.getText().trim(), txtNueva.getText().trim(),
                    txtRepetirNueva.getText().trim());
            Alerta.mostrarMensaje(false, "Info", "Se ha cambiado la contraseña del miembro correctamente!");
            StageManager.cerrarModal(Vista.CambiarContraseña);
        } catch (Exception e) {
            Alerta.mostrarMensaje(true, "Error", "No se pudo cambiar la contraseña.\n" + e.getMessage());
        }
    }
}
