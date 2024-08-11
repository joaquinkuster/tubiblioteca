package com.tubiblioteca.controller;

import com.tubiblioteca.view.VistasFXML;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;
import com.tubiblioteca.App;

public class VistaPrincipalControlador implements Initializable {
    // BorderPane del contenedor principal
    @FXML
    private BorderPane bpVistaPrincipal;

    private VistasFXML vistaCentro;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Establecemos la vista central inicial
        if (bpVistaPrincipal.getCenter() == null) {
            vistaCentro = VistasFXML.ListaMiembros;
            Node centroInicial = App.cargarVista(vistaCentro.getRutaFxml());
            bpVistaPrincipal.setCenter(centroInicial);
        }
    }

    public void cambiarCentro(VistasFXML vista) {
        // Guardamos la vista del nuevo centro
        vistaCentro = vista;

        // Obtenemos el nodo raiz del centro actual del contenedor
        Node centroActual = bpVistaPrincipal.getCenter();
        // Obtenemos el nodo raiz del nuevo centro a colocar en el contenedor
        Node nuevoCentro = App.cargarVista(vista.getRutaFxml());

        // Cambiamos el título de la ventana principal
        App.setTitulo(vista.getTitulo());
        // Establecemos la opacidad en 0 del nuevo centro para que aparezca inicialmente invisible
        nuevoCentro.setOpacity(0);

        // Creamos una transición de desvanecimiento para cambiar el centro del contenedor
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
            // Al terminar el desvanecimiento de salida, establecemos el nuevo centro (invisible)
            bpVistaPrincipal.setCenter(nuevoCentro);

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

    public VistasFXML getVistaCentro(){
        return vistaCentro;
    }
}
