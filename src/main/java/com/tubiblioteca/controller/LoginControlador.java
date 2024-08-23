package com.tubiblioteca.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.security.SesionManager;
import com.tubiblioteca.service.Miembro.MiembroServicio;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LoginControlador implements Initializable {

    // Variables FXML
    @FXML
    private TextField txtDni;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnEntrar;

    @FXML
    private CheckBox checkRecordar;

    // Dependencias
    private final Preferences credenciales = Preferences.userNodeForPackage(LoginControlador.class);
    private MiembroServicio servicio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicio = new MiembroServicio(AppConfig.getRepositorio());
        txtDni.requestFocus();
        autocargarCredenciales();
        ControlUI.configurarAtajoTecladoEnter(btnEntrar);
    }

    @FXML
    private void iniciarSesion() {
        String dni = txtDni.getText().trim();
        String clave = txtContrasena.getText().trim();

        try{
            if (servicio.validarCredenciales(dni, clave)){
                if (checkRecordar.isSelected()) {
                    guardarCredenciales(dni, clave);
                }
                Miembro miembro = servicio.buscarPorId(dni);
                SesionManager.abrirSesion(miembro);
            }
        } catch (Exception e) {
            Alerta.mostrarMensaje(true, "Error", e.getMessage());
        }
    }

    @FXML
    private void olvidasteContrasena() {
        Alerta.mostrarMensaje(false, "Info", "Por favor, comunícate con un bibliotecario para obtener ayuda y recuperar el acceso a tu cuenta.");
    }

    // Métodos privados

    /**
     * Carga automáticamente las credenciales guardadas, si existen.
     */
    private void autocargarCredenciales() {
        txtDni.setText(credenciales.get("dni", ""));
        txtContrasena.setText(credenciales.get("contrasena", ""));
    }

    /**
     * Guarda las credenciales del usuario en las preferencias del sistema.
     * 
     * @param dni        El DNI del usuario.
     * @param contrasena La contraseña del usuario.
     */
    private void guardarCredenciales(String dni, String contrasena) {
        credenciales.put("dni", dni);
        credenciales.put("contrasena", contrasena);
    }
}
