package com.tubiblioteca.controller;

import com.tubiblioteca.view.VistasFXML;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import com.tubiblioteca.helper.Alerta;

public class MenuControlador implements Initializable {

    // Componentes para mostrar la información del usuario
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblTipo;

    // Botones del menu lateral
    @FXML
    private Button btnMiembros, btnAuditoria, btnEditoriales, btnCategorias, btnCopiaLibros, btnRacks, btnLibros, btnIdiomas, btnPrestamos;
    @FXML
    private HBox hboxPerfil;

    // Controladores de los fxml
    private VistaPrincipalControlador vistaPrincipalControlador;

    // Lista de items (botones) del menu
    private List<Button> items;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Agregamos los listeners a los botones
        addListeners();

        // Inicializamos la lista de items con los botones correspondientes
        items = Arrays.asList(btnMiembros, btnAuditoria, btnEditoriales, btnCategorias, btnCopiaLibros, btnRacks, btnLibros, btnIdiomas, btnPrestamos);

        // Actualizamos la información del usuario
        //actualizarMenu();

        // Establecemos un handler cuando se presiona Enter en el Vbox del perfil
        // Establecemos foco en boton de inicio
        hboxPerfil.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Llamar a la función que deseas ejecutar aquí
                modificarPerfil();
            }
        });
        Platform.runLater(() -> btnMiembros.requestFocus());

    }

    // Listeners de los items del menu

    private void addListeners() {
        // Establecemos los handlers correspondientes a cada boton
        btnMiembros.setOnAction(event -> redireccionarMenu(VistasFXML.ListaMiembros, (Button) event.getSource()));
        btnAuditoria.setOnAction(null);
        btnCategorias.setOnAction(null);
        btnCopiaLibros.setOnAction(null);
        btnEditoriales.setOnAction(event -> redireccionarMenu(VistasFXML.ListaEditoriales, (Button) event.getSource()));
        btnIdiomas.setOnAction(null);
        btnLibros.setOnAction(null);
        btnPrestamos.setOnAction(null);
        btnRacks.setOnAction(null);
    }

    // Meotodo para cambiar el contenido central del main

    public void redireccionarMenu(VistasFXML vista, Button btn) {
        // Verificamos si el boton no tiene la clase actual, lo que significa que no está actualmente seleccionado
        if (!btn.getStyleClass().contains("actual")) {
            // Itera sobre la lista de items y remueve la clase actual de cada boton, si la tienen
            items.forEach(button -> button.getStyleClass().removeIf(style -> style.equals("actual")));
            // Agregamos la clase actual al boton que disparo el evento
            btn.getStyleClass().add("actual");

            // Cambiamos el contenido del centro del contenedor principal
            vistaPrincipalControlador.cambiarCentro(vista);
        }
    }

    // Metodo para cerrar sesion

    @FXML
    private void cerrarSesion() {
        if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea cerrar sesión?")) {
            // Cerramos la sesion
            //sessionManager.cerrarSesion();
        }
    }

    // Metodo para modificar el perfil del usuario en sesion

    @FXML
    private void modificarPerfil() {
        //if (sessionManager.validarSesion()) {
            //miembroManager.cargarFormulario(sessionManager.getUsuario(), this);
        //}
    }

    // Metodo para establecer la información del usuario en el menu

    /*public void actualizarMenu() {
        if (sessionManager.validarSesion()) {

            Miembro miembro = sessionManager.getUsuario();

            // Obtenemos la información del usuario en sesion
            byte[] imagenBytes = miembro.getFoto();
            String nombre = miembro.toString();
            Cargo cargo = miembro.getCargo() != null ? miembro.getCargo() : null;

            // Verificamos si tiene foto de perfil
            if (imagenBytes != null) {

                // Convertimos los bytes en un Image
                Image imagen = ImageHelper.convertirBytesAImage(imagenBytes);

                // Si la conversion fue exitosa, establecemos la foto de perfil
                if (imagen != null) {
                    fotoPerfil.setImage(imagen);
                } else {
                    // Colocamos la foto de perfil por defecto
                    ImageHelper.colocarImagenPorDefecto(fotoPerfil);
                }
            } else {
                // Colocamos la foto de perfil por defecto
                ImageHelper.colocarImagenPorDefecto(fotoPerfil);
            }

            // Redondeamos la imagen
            ImageHelper.redondearImagen(fotoPerfil);

            // Establecemos el nombre y cargo del miembro
            if (nombre != null) {
                lblNombre.setText(nombre);
            }
            if (cargo != null) {
                lblCargo.setText(cargo.toString());

                // Mostramos / ocultamos el boton de miembros basandonos en la proridad
                habilitarBtnMiembros(cargo.getPrioridad() >= 2);
            }
        }
    }*/

    // Metodo para ocultar / mostrar btn de miembros segun la prioridad del miembro

    /*public void habilitarBtnMiembros(boolean tieneAcceso) {
        btnMiembros.setDisable(!tieneAcceso);
        btnMiembros.setVisible(tieneAcceso);
    }*/
}
