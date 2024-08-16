package com.tubiblioteca.controller;

import com.tubiblioteca.view.Vista;

import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Node;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;

public class MenuControlador implements Initializable {

    // Componentes para mostrar la información del usuario
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblTipo;

    // Botones del menu lateral
    @FXML
    private Button btnMiembros, btnAutores, btnAuditoria, btnEditoriales, btnCategorias, btnCopiaLibros, btnRacks, btnLibros, btnIdiomas, btnPrestamos;
    @FXML
    private HBox hboxPerfil;

    private BorderPane panel;

    // Lista de items (botones) del menu
    private List<Button> items;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Agregamos los listeners a los botones
        addListeners();

        // Inicializamos la lista de items con los botones correspondientes
        items = Arrays.asList(btnMiembros, btnAutores, btnAuditoria, btnEditoriales, btnCategorias, btnCopiaLibros, btnRacks, btnLibros, btnIdiomas, btnPrestamos);

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

    public void setPanel(BorderPane panel){
        this.panel = panel;

        if (panel.getCenter() == null) {
            panel.setCenter(StageManager.cargarVista(Vista.ListaMiembros.getRutaFxml()));
        }
    }

    // Listeners de los items del menu

    private void addListeners() {
        // Establecemos los handlers correspondientes a cada boton
        btnMiembros.setOnAction(event -> redireccionarMenu(Vista.ListaMiembros, (Button) event.getSource()));
        btnAuditoria.setOnAction(null);
        btnCategorias.setOnAction(null);
        btnCopiaLibros.setOnAction(null);
        btnEditoriales.setOnAction(event -> redireccionarMenu(Vista.ListaEditoriales, (Button) event.getSource()));
        btnIdiomas.setOnAction(null);
        btnLibros.setOnAction(null);
        btnPrestamos.setOnAction(null);
        btnRacks.setOnAction(null);
        btnAutores.setOnAction(event -> redireccionarMenu(Vista.ListaAutores, (Button) event.getSource()));
    }

    // Meotodo para cambiar el contenido central del main

    public void redireccionarMenu(Vista vista, Button btn) {
        // Verificamos si el boton no tiene la clase actual, lo que significa que no está actualmente seleccionado
        if (!btn.getStyleClass().contains("actual")) {
            // Itera sobre la lista de items y remueve la clase actual de cada boton, si la tienen
            items.forEach(button -> button.getStyleClass().removeIf(style -> style.equals("actual")));
            // Agregamos la clase actual al boton que disparo el evento
            btn.getStyleClass().add("actual");

            // Cambiamos el contenido del centro del contenedor principal
            cambiarCentro(vista);
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

public void cambiarCentro(Vista vista) {
        // Obtenemos el nodo raiz del centro actual del contenedor
        Node centroActual = panel.getCenter();
        // Obtenemos el nodo raiz del nuevo centro a colocar en el contenedor
        Node nuevoCentro = StageManager.cargarVista(vista.getRutaFxml());

        // Establecemos la nueva vista que se seleccionó
        panel.setCenter(nuevoCentro);

        // Cambiamos el título de la ventana principal
        StageManager.setTitulo(vista.getTitulo());
        // Establecemos la opacidad en 0 del nuevo centro para que aparezca inicialmente
        // invisible
        nuevoCentro.setOpacity(0);

        // Creamos una transición de desvanecimiento para cambiar el centro del
        // contenedor
        FadeTransition fadeOut = crearTransition(centroActual, nuevoCentro);
        // Iniciamos la transicion de salida
        fadeOut.play();
    }

    private FadeTransition crearTransition(Node centroActual, Node nuevoCentro) {
        // Creamos una transición de desvanecimiento de salida para el centro actual
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), centroActual);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            // Al terminar el desvanecimiento de salida, establecemos el nuevo centro
            // (invisible)
            panel.setCenter(nuevoCentro);

            // Creamos una transición de desvanecimiento de entrada para el nuevo centro
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), nuevoCentro);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            // Iniciamos la transicion de entrada
            fadeIn.play();
        });

        // Devolvemos la transicion de salida
        return fadeOut;
    }
}
