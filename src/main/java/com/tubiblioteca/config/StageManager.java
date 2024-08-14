package com.tubiblioteca.config;

import com.tubiblioteca.App;
import com.tubiblioteca.view.Vista;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class StageManager {

    private static final Logger log = LoggerFactory.getLogger(StageManager.class);
    private static Stage stagePrincipal;
    private static Map<String, Stage> modalStages = new HashMap<>();

    // Cambia la escena principal a la especificada por Vista
    public static void cambiarEscena(final Vista vista) {
        Parent root = cargarVista(vista.getRutaFxml());
        if (root != null) {
            mostrar(root, vista.getTitulo());
        } else {
            log.error("No se pudo cargar la vista: " + vista.getRutaFxml());
        }
    }

    // Muestra la escena en el Stage principal
    private static void mostrar(final Parent root, String titulo) {
        Scene escena = prepararEscena(root);

        if (stagePrincipal != null) {
            stagePrincipal.hide();
            stagePrincipal.setTitle(titulo);
            stagePrincipal.setScene(escena);
            stagePrincipal.sizeToScene();
            stagePrincipal.centerOnScreen();
            stagePrincipal.setResizable(false);
            stagePrincipal.getIcons().add(new Image(App.class.getResource("images/logo.png").toExternalForm()));

            try {
                stagePrincipal.show();
            } catch (Exception e) {
                log.error("No se puede mostrar la escena del título: " + titulo, e);
            }
        } else {
            log.error("El Stage principal no está inicializado.");
        }
    }

    // Prepara la escena actual o crea una nueva si no existe
    private static Scene prepararEscena(Parent root) {
        Scene escena = stagePrincipal.getScene();
        if (escena == null) {
            escena = new Scene(root);
        } else {
            escena.setRoot(root);
        }
        return escena;
    }

    // Abre un modal con la vista proporcionada
    public static void abrirModal(Parent root, Vista vista) {
        Stage modalStage = crearModalStage(vista);
        mostrarModal(modalStage, root, vista);
    }

    // Crea un nuevo Stage para el modal
    private static Stage crearModalStage(Vista vista) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStages.put(vista.getTitulo(), modalStage);
        return modalStage;
    }

    // Cierra un modal asociado al título proporcionado
    public static void cerrarModal(Vista vista) {
        Stage modalStage = modalStages.get(vista.getTitulo());
        if (modalStage != null) {
            modalStage.close();
            modalStages.remove(vista.getTitulo());
        } else {
            log.warn("No se encontró un modal con el título: " + vista.getTitulo());
        }
    }

    // Muestra el modal con la vista proporcionada
    private static void mostrarModal(Stage modalStage, Parent root, Vista vista) {
        Scene escena = new Scene(root);
        modalStage.setTitle(vista.getTitulo());
        modalStage.setScene(escena);
        modalStage.sizeToScene();
        modalStage.centerOnScreen();
        modalStage.setResizable(false);
        modalStage.getIcons().add(new Image(App.class.getResource("images/logo.png").toExternalForm()));
        modalStage.setOnCloseRequest(event -> cerrarModal(vista));
        modalStage.showAndWait();
    }

    // Carga una vista FXML y devuelve el nodo raíz
    public static Parent cargarVista(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(rutaFxml));
            Parent root = loader.load();
            Objects.requireNonNull(root, "El nodo FXML raíz no debe ser nulo");
            return root;
        } catch (IOException e) {
            log.error("Error al cargar la vista FXML: " + rutaFxml, e);
            return null;
        }
    }

    // Carga una vista FXML para un modal y devuelve el controlador y el nodo raíz
    public static <T> Pair<T, Parent> cargarVistaConControlador(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent nodo = loader.load();
            T controlador = loader.getController();
            return new Pair<>(controlador, nodo);
        } catch (IOException e) {
            log.error("Error al cargar el modal FXML: " + fxmlPath, e);
            return null;
        }
    }

    // Establece el Stage principal
    public static void setStagePrincipal(Stage stage) {
        stagePrincipal = stage;
    }

    // Coloca un titulo al Stage
    public static void setTitulo(String titulo) {
        stagePrincipal.setTitle(titulo);
    }
}
