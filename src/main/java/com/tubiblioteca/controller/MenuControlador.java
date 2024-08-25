package com.tubiblioteca.controller;

import com.tubiblioteca.view.Vista;

import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.Parent;

import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.security.SesionManager;

public class MenuControlador implements Initializable {

    // Componentes para mostrar la información del usuario
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblTipo;

    // Botones del menu lateral
    @FXML
    private Button btnMiembros, btnAutores, btnAuditoria, btnEditoriales, btnCategorias, btnCopiaLibros, btnRacks,
            btnLibros, btnIdiomas, btnPrestamos;
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
        items = Arrays.asList(btnMiembros, btnAutores, btnAuditoria, btnEditoriales, btnCategorias, btnCopiaLibros,
                btnRacks, btnLibros, btnIdiomas, btnPrestamos);

        // Actualizamos la información del usuario
        actualizarMenu();

        Platform.runLater(() -> btnPrestamos.requestFocus());
    }

    public void setPanel(BorderPane panel) {
        this.panel = panel;

        if (panel.getCenter() == null) {
            panel.setCenter(StageManager.cargarVista(Vista.ListaPrestamos.getRutaFxml()));
        }
    }

    // Listeners de los items del menu

    private void addListeners() {
        // Establecemos los handlers correspondientes a cada boton
        btnPrestamos.setOnAction(event -> redireccionarMenu(Vista.ListaPrestamos, (Button) event.getSource()));
        btnLibros.setOnAction(event -> redireccionarMenu(Vista.ListaLibros, (Button) event.getSource()));
        btnCopiaLibros.setOnAction(event -> redireccionarMenu(Vista.ListaCopiasLibros, (Button) event.getSource()));
        btnMiembros.setOnAction(event -> redireccionarMenu(Vista.ListaMiembros, (Button) event.getSource()));
        btnRacks.setOnAction(event -> redireccionarMenu(Vista.ListaRacks, (Button) event.getSource()));
        btnEditoriales.setOnAction(event -> redireccionarMenu(Vista.ListaEditoriales, (Button) event.getSource()));
        btnAutores.setOnAction(event -> redireccionarMenu(Vista.ListaAutores, (Button) event.getSource()));
        btnCategorias.setOnAction(event -> redireccionarMenu(Vista.ListaCategorias, (Button) event.getSource()));
        btnIdiomas.setOnAction(event -> redireccionarMenu(Vista.ListaIdiomas, (Button) event.getSource()));
        btnAuditoria.setOnAction(event -> redireccionarMenu(Vista.ListaAuditoria, (Button) event.getSource()));
    }

    // Meotodo para cambiar el contenido central del main

    public void redireccionarMenu(Vista vista, Button btn) {
        // Verificamos si el boton no tiene la clase actual, lo que significa que no
        // está actualmente seleccionado
        if (!btn.getStyleClass().contains("actual")) {
            // Itera sobre la lista de items y remueve la clase actual de cada boton, si la
            // tienen
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
            SesionManager.cerrarSesion();
        }
    }

    // Metodo para modificar la contrasena

    @FXML
    private void cambiarContrasena() {
        Parent vista = StageManager.cargarVista(Vista.CambiarContraseña.getRutaFxml()); 
        StageManager.abrirModal(vista, Vista.CambiarContraseña);
    }

    // Metodo para establecer la información del usuario en el menu

    public void actualizarMenu() {
        if (SesionManager.haySesion()) {

            Miembro miembro = SesionManager.getMiembro();

            // Obtenemos la información del usuario en sesion
            String nombre = miembro.toString();
            TipoMiembro tipo = miembro.getTipo() != null ? miembro.getTipo() : null;

            // Establecemos el nombre y cargo del miembro
            if (nombre != null) {
                lblNombre.setText(nombre);
            }
            if (tipo != null) {
                lblTipo.setText(tipo.toString());
            }

            if (SesionManager.esUsuario()) {
                btnPrestamos.setText("Mis Préstamos");
                ControlUI.desactivarControl(btnMiembros, btnCopiaLibros, btnRacks, btnEditoriales, btnAutores,
                        btnCategorias, btnIdiomas, btnAuditoria);
            }
        }
    }

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
