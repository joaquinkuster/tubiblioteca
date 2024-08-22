package com.tubiblioteca.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.view.Vista;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginControlador implements Initializable {

    // Campos del formulario
    @FXML
    private TextField txtDni;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private Button btnEntrar;

    // CheckBox para recordar los credenciales
    @FXML
    private CheckBox checkRecordar;

    // Logger para mostrar informacion
    private final Logger log = LoggerFactory.getLogger(LoginControlador.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtDni.requestFocus();
        ControlUI.configurarAtajoTecladoEnter(btnEntrar);
    }

    @FXML
    private void iniciarSesion() {
        StageManager.cambiarEscena(Vista.VistaPrincipal);
    }

    @FXML
    private void crearCuenta() {

    }

    private boolean validarCamposFormulario() {
        return true;
    }

    @FXML
    private void olvidasteContrasena() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Info");
        alert.setContentText(
                "Por favor, comunícate con el personal administrativo para obtener ayuda y recuperar el acceso a tu cuenta.");
        alert.showAndWait();
    }
}