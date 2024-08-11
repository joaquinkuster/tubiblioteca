package com.tubiblioteca;

import java.io.IOException;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.GestorDePantallas;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.view.VistasFXML;
import jakarta.persistence.Persistence;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import javafx.application.Platform;
import javafx.scene.Parent;

public class App extends Application {

    // Utilizada para gestionar las vistas en un mismo escenario
    private static GestorDePantallas gestorDePantallas;

    // Utilizado como repositorio compartido
    private static Repositorio repositorio;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(App.class);

    @Override
    public void init() {
        log.info("Inicializando TuBiblioteca...");
    }

    @Override
    public void start(Stage stage) throws IOException {
        log.info("Iniciando TuBiblioteca...");

        // creación del manejador de la conexión
        var emf = Persistence.createEntityManagerFactory("TuBibliotecaPU");
        // crea el repositorio 
        repositorio = new Repositorio(emf);

        // carga la escena principal
        log.info("Cargando la ventana principal...");
        gestorDePantallas = new GestorDePantallas(stage);
        gestorDePantallas.cambiarEscena(VistasFXML.Login);
    }

    @Override
    public void stop() {
        log.info("Deteniendo TuBiblioteca...");
        // Cerramos la aplicacion de forma ordenada
        Platform.exit();
    }

    public static void cambiarEscena(VistasFXML vista) {
        gestorDePantallas.cambiarEscena(vista);
    }

    public static void setTitulo(String titulo) {
        gestorDePantallas.setTitulo(titulo);
    }

    public static Parent cargarVista(String rutaFxml) {
        return gestorDePantallas.cargarVista(rutaFxml);
    }

    public static void abrirModal(Parent root, VistasFXML vista) {
        gestorDePantallas.abrirModal(root, vista);
    }

    public static void cerrarmodal(VistasFXML vista) {
        gestorDePantallas.cerrarModal(vista);
    }

    public static Repositorio getRepositorio() {
        return repositorio;
    }

    public static void setRepositorio(Repositorio rep) {
        repositorio = rep;
    }

    public static void main(String[] args) {
        launch();
    }
}