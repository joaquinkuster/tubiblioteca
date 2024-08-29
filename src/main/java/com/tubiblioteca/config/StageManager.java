package com.tubiblioteca.config;

import com.tubiblioteca.App;
import com.tubiblioteca.view.Vista;
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

public class StageManager {

    // Logger para registrar eventos y errores
    private static final Logger log = LoggerFactory.getLogger(StageManager.class);

    // Stage principal de la aplicación
    private static Stage stagePrincipal;

    // Mapa para gestionar los modales abiertos, asociando títulos con Stages
    private static Map<String, Stage> modalStages = new HashMap<>();

    // Cambia la escena principal a la especificada por la vista proporcionada
    public static void cambiarEscena(Vista vista) {
        Parent root = cargarVista(vista.getRutaFxml()); // Carga la vista FXML
        if (root != null) {
            mostrar(root, vista.getTitulo()); // Muestra la vista en el Stage principal
        } else {
            log.error("No se pudo cargar la vista: " + vista.getRutaFxml()); // Registra un error si la vista no se carga
        }
    }

    // Muestra la escena en el Stage principal
    private static void mostrar(final Parent root, String titulo) {
        Scene escena = prepararEscena(root); // Prepara o reutiliza la escena

        if (stagePrincipal != null) {
            // Configura y muestra la escena en el Stage principal
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
                log.error("No se puede mostrar la escena del título: " + titulo, e); // Registra un error si no se puede mostrar la escena
            }
        } else {
            log.error("El Stage principal no está inicializado."); // Registra un error si el Stage principal no está configurado
        }
    }

    // Prepara la escena actual o crea una nueva si no existe
    private static Scene prepararEscena(Parent root) {
        Scene escena = stagePrincipal.getScene(); // Obtiene la escena actual del Stage principal
        if (escena == null) {
            escena = new Scene(root); // Crea una nueva escena si no existe
        } else {
            escena.setRoot(root); // Establece el nuevo root en la escena existente
        }
        return escena;
    }

    // Abre un modal con la vista proporcionada
    public static void abrirModal(Parent root, Vista vista) {
        Stage modalStage = crearModalStage(vista); // Crea un nuevo Stage para el modal
        mostrarModal(modalStage, root, vista); // Muestra el modal
    }

    // Crea un nuevo Stage para el modal
    private static Stage crearModalStage(Vista vista) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL); // Configura el modal como bloqueante
        modalStages.put(vista.getTitulo(), modalStage); // Almacena el modal en el mapa con su título
        return modalStage;
    }

    // Cierra un modal asociado al título proporcionado
    public static void cerrarModal(Vista vista) {
        Stage modalStage = modalStages.get(vista.getTitulo()); // Obtiene el modal del mapa
        if (modalStage != null) {
            modalStage.close(); // Cierra el modal si existe
            modalStages.remove(vista.getTitulo()); // Elimina el modal del mapa
        } else {
            log.warn("No se encontró un modal con el título: " + vista.getTitulo()); // Advierte si no se encuentra el modal
        }
    }

    // Muestra el modal con la vista proporcionada
    private static void mostrarModal(Stage modalStage, Parent root, Vista vista) {
        Scene escena = new Scene(root); // Crea una nueva escena para el modal
        modalStage.setTitle(vista.getTitulo()); // Establece el título del modal
        modalStage.setScene(escena);
        modalStage.sizeToScene();
        modalStage.centerOnScreen();
        modalStage.setResizable(false);
        modalStage.getIcons().add(new Image(App.class.getResource("images/logo.png").toExternalForm()));
        modalStage.setOnCloseRequest(event -> cerrarModal(vista)); // Configura el cierre del modal
        modalStage.showAndWait(); // Muestra el modal y espera hasta que se cierre
    }

    // Carga una vista FXML y devuelve el nodo raíz
    public static Parent cargarVista(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(rutaFxml)); // Carga el archivo FXML
            Parent root = loader.load(); // Carga el nodo raíz de la vista
            Objects.requireNonNull(root, "El nodo FXML raíz no debe ser nulo"); // Verifica que el nodo no sea nulo
            return root;
        } catch (IOException e) {
            log.error("Error al cargar la vista FXML: " + rutaFxml, e); // Registra un error si falla la carga
            return null;
        }
    }

    // Carga una vista FXML para un modal y devuelve el controlador y el nodo raíz
    public static <T> Pair<T, Parent> cargarVistaConControlador(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath)); // Carga el archivo FXML
            Parent nodo = loader.load(); // Carga el nodo raíz de la vista
            T controlador = loader.getController(); // Obtiene el controlador asociado
            return new Pair<>(controlador, nodo); // Devuelve el controlador y el nodo raíz como un par
        } catch (IOException e) {
            log.error("Error al cargar el modal FXML: " + fxmlPath, e); // Registra un error si falla la carga
            return null;
        }
    }

    // Establece el Stage principal de la aplicación
    public static void setStagePrincipal(Stage stage) {
        stagePrincipal = stage;
    }

    // Coloca un título al Stage principal
    public static void setTitulo(String titulo) {
        stagePrincipal.setTitle(titulo);
    }
}
