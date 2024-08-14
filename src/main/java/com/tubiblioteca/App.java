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

    private final Logger log = LoggerFactory.getLogger(App.class);

    @Override
    public void init() {
        log.info("Inicializando TuBiblioteca...");
        try {
            var emf = Persistence.createEntityManagerFactory("TuBibliotecaPU");
            AppConfig.setRepositorio(new Repositorio(emf));
        } catch (Exception e) {
            log.error("Error al inicializar el EntityManagerFactory", e);
            Platform.exit(); // Termina la aplicación si no se puede inicializar el repositorio
        }
    }

    @Override
    public void start(Stage stage) {
        log.info("Iniciando TuBiblioteca...");
        StageManager.setStagePrincipal(stage);
        StageManager.cambiarEscena(Vista.Login);
    }

    @Override
    public void stop() {
        log.info("Deteniendo TuBiblioteca...");
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}