package com.tubiblioteca.controladores;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoginControlador implements Initializable {

    // Campos del formulario
    @FXML
    private TextField txtDni;
    @FXML
    private PasswordField txtContrasena;

    // CheckBox para recordar los credenciales
    @FXML
    private CheckBox checkRecordar;

    // Logger para mostrar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(LoginControlador.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtDni.requestFocus();
    }

    @FXML
    private void iniciarSesion() {

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
        alert.setContentText("Por favor, comunícate con el personal administrativo para obtener ayuda y recuperar el acceso a tu cuenta.");
        alert.showAndWait();
    }
}