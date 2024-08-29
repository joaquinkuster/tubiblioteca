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
    private TextField txtDni; // Campo para ingresar el DNI del usuario

    @FXML
    private PasswordField txtContrasena; // Campo para ingresar la contraseña del usuario

    @FXML
    private Button btnEntrar; // Botón para iniciar sesión

    @FXML
    private CheckBox checkRecordar; // Checkbox para recordar las credenciales del usuario

    // Dependencias
    private final Preferences credenciales = Preferences.userNodeForPackage(LoginControlador.class); // Preferencias para almacenar credenciales
    private MiembroServicio servicio; // Servicio para manejar las operaciones relacionadas con los miembros

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialización del servicio de miembros utilizando el repositorio de configuración
        servicio = new MiembroServicio(AppConfig.getRepositorio());
        txtDni.requestFocus(); // Establece el foco en el campo de DNI al iniciar
        autocargarCredenciales(); // Carga las credenciales guardadas, si existen
        ControlUI.configurarAtajoTecladoEnter(btnEntrar); // Configura el botón para que se active con la tecla Enter
    }

    @FXML
    private void iniciarSesion() {
        // Obtiene el DNI y la contraseña ingresados por el usuario
        String dni = txtDni.getText().trim();
        String clave = txtContrasena.getText().trim();
        Miembro miembro = servicio.buscarPorId(dni); // Busca al miembro en el servicio

        try {
            // Verifica las credenciales del usuario
            if (servicio.validarCredenciales(dni, clave)) {
                // Si el checkbox está seleccionado, guarda las credenciales
                if (checkRecordar.isSelected()) {
                    guardarCredenciales(dni, clave);
                }
                // Abre una sesión para el miembro
                SesionManager.abrirSesion(miembro);
            }
        } catch (Exception e) {
            // Muestra un mensaje de error en caso de excepción
            Alerta.mostrarMensaje(true, "Error", e.getMessage());
        }
    }

    @FXML
    private void olvidasteContrasena() {
        // Muestra un mensaje de información si el usuario olvida su contraseña
        Alerta.mostrarMensaje(false, "Info", "Por favor, comunícate con un bibliotecario para obtener ayuda y recuperar el acceso a tu cuenta.");
    }

    // Métodos privados

    /**
     * Carga automáticamente las credenciales guardadas, si existen.
     */
    private void autocargarCredenciales() {
        // Obtiene el DNI y la contraseña guardados en las preferencias y los coloca en los campos correspondientes
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
        // Guarda el DNI y la contraseña en las preferencias del sistema
        credenciales.put("dni", dni);
        credenciales.put("contrasena", contrasena);
    }
}
