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

    // Campos del formulario de cambio de contraseña
    @FXML
    private PasswordField txtActual;  // Campo para la contraseña actual
    @FXML
    private PasswordField txtNueva;   // Campo para la nueva contraseña
    @FXML
    private PasswordField txtRepetirNueva; // Campo para repetir la nueva contraseña
    @FXML
    private Button btnGuardar; // Botón para guardar los cambios

    private MiembroServicio servicio; // Servicio para gestionar los miembros

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialización del servicio de miembros utilizando el repositorio de configuración
        servicio = new MiembroServicio(AppConfig.getRepositorio());

        // Configura el botón para que se active con la tecla Enter
        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    @FXML
    private void guardar() {
        try {
            // Intenta cambiar la contraseña utilizando el servicio
            servicio.cambiarClave(txtActual.getText().trim(), txtNueva.getText().trim(),
                    txtRepetirNueva.getText().trim());
            // Muestra un mensaje de éxito si el cambio de contraseña fue exitoso
            Alerta.mostrarMensaje(false, "Info", "Se ha cambiado la contraseña del miembro correctamente!");
            // Cierra el modal de cambio de contraseña
            StageManager.cerrarModal(Vista.CambiarContraseña);
        } catch (Exception e) {
            // Muestra un mensaje de error si ocurre una excepción al cambiar la contraseña
            Alerta.mostrarMensaje(true, "Error", "No se pudo cambiar la contraseña.\n" + e.getMessage());
        }
    }
}
