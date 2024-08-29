package com.tubiblioteca;

import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.view.Vista;
import jakarta.persistence.Persistence;
import javafx.application.Application;
import org.slf4j.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;

public class App extends Application {

    // Logger para registrar información sobre el estado de la aplicación
    private final Logger log = LoggerFactory.getLogger(App.class);

    // Método que se ejecuta al iniciar la aplicación, antes de start()
    @Override
    public void init() {
        log.info("Inicializando TuBiblioteca...");
        try {
            // Crea una instancia de EntityManagerFactory utilizando una unidad de persistencia definida
            var emf = Persistence.createEntityManagerFactory("TuBibliotecaPU");

            // Establece el repositorio en AppConfig usando la instancia del EntityManagerFactory
            AppConfig.setRepositorio(new Repositorio(emf));
        } catch (Exception e) {
            // Registra un error y cierra la aplicación si ocurre un problema al inicializar el EntityManagerFactory
            log.error("Error al inicializar el EntityManagerFactory", e);
            Platform.exit(); // Cierra la aplicación
        }
    }

    // Método que se ejecuta al iniciar la interfaz gráfica de la aplicación
    @Override
    public void start(@SuppressWarnings("exports") Stage stage) {
        log.info("Iniciando TuBiblioteca...");

        // Establece el stage principal en StageManager
        StageManager.setStagePrincipal(stage);

        // Cambia la escena a la vista de Login
        StageManager.cambiarEscena(Vista.Login);
    }

    // Método que se ejecuta cuando se detiene la aplicación
    @Override
    public void stop() {
        log.info("Deteniendo TuBiblioteca...");
        Platform.exit(); // Cierra la aplicación
    }

    // Método main que inicia la aplicación JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}
