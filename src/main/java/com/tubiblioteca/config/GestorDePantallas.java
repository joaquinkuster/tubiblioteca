package com.tubiblioteca.config;

import com.tubiblioteca.view.VistasFXML;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static org.slf4j.LoggerFactory.getLogger;

public class GestorDePantallas {

    // Logger para gestionar informacion
    private static final Logger log = getLogger(GestorDePantallas.class);

    // Stage principal de la aplicacion
    public final Stage stagePrincipal;

    // Mapa de modales asociados a su titulo (clave)
    public final Map<String, Stage> modalStages = new HashMap<>();

    public GestorDePantallas(Stage stage) {
        this.stagePrincipal = stage;
    }

    // Recibimos un FXMLView que contiene información sobre la vista a cargar
    public void cambiarEscena(final VistasFXML vista) {
        // Cargamos la vista FXML segun su ruta
        Parent root = cargarVista(vista.getRutaFxml());

        // Mostramos la vista FXML
        mostrar(root, vista.getTitulo());
    }

    private void mostrar(final Parent root, String titulo) {
        // Preparamos la escena y le pasamos el nodo raiz de la vista FXML cargada
        Scene escena = prepararEscena(root);
        //scene.getStylesheets().add("/styles/_styles.css");
        //primaryStage.initStyle(StageStyle.TRANSPARENT);

        // Ocultamos la ventana principal para realizar las configuraciones necesarias
        stagePrincipal.hide();

        // Configuramos la ventana principal
        stagePrincipal.setTitle(titulo);
        stagePrincipal.setScene(escena);
        stagePrincipal.sizeToScene();
        stagePrincipal.centerOnScreen();
        stagePrincipal.setResizable(false);
        stagePrincipal.getIcons().add(new Image(("ui/images/logo.png")));

        try {
            // Mostramos el Stage después de la animación
            stagePrincipal.show();

        } catch (Exception e) {
            cerrar("No se puede mostrar la escena del título: " + titulo, e);
        }
    }

    private Scene prepararEscena(Parent root) {
        // Obtenemos la escena actual de la ventana principal
        Scene escena = stagePrincipal.getScene();

        // Si la escena es nula, creamos una nueva utilizando el nodo raiz proporcioando
        if (escena == null) {
            escena = new Scene(root);
        }

        System.out.println(escena.getRoot());

        // Cambiamos el nodo raiz de la escena
        escena.setRoot(root);

        // Devolvemos la escena
        return escena;
    }

    /**
     *
     */

    public void abrirModal(Parent root, VistasFXML vista) {
        // Creamos un nuevo modal y le pasamos el título deseado
        Stage modalStage = crearModalStage(vista);

        // Mostramos el modal creado respectivo con el nodo raiz y título deseado
        mostrarModal(modalStage, root, vista);
    }

    private Stage crearModalStage(VistasFXML vista) {
        // Creamos una nueva ventana
        Stage modalStage = new Stage();

        // Establecemos la modalidad de la ventana
        modalStage.initModality(Modality.APPLICATION_MODAL);

        // Guardamos este modal en el mapa utilizando el título como clave
        modalStages.put(vista.getTitulo(), modalStage);

        // Devolvemos el modal creado
        return modalStage;
    }

    public void cerrarModal(VistasFXML vista) {
        // Buscamos el modal correspondiente en el mapa utilizando el título como clave
        Stage modalStage = modalStages.get(vista.getTitulo());

        // Verificamos que el modal sea diferente de nulo
        if (modalStage != null) {
            // Cerramos el mdoal
            modalStage.close();

            // Eliminamos el modal del mapa
            modalStages.remove(vista.getTitulo());
        }
    }

    private void mostrarModal(Stage modalStage, Parent root, VistasFXML vista) {
        // Creamos una nueva escena a partir del nodo raiz proporcionado
        Scene escena = new Scene(root);

        // Configuramos el modal proporcionado
        modalStage.setTitle(vista.getTitulo());
        modalStage.setScene(escena);
        modalStage.sizeToScene();
        modalStage.centerOnScreen();
        modalStage.setResizable(false);
        modalStage.getIcons().add(new Image(("ui/images/logo.png")));

        // Cuando se cierre el modal, automaticamente lo elimine del mapa
        modalStage.setOnCloseRequest(event ->
                cerrarModal(vista)
        );

        // Mostramos la ventana de manera modal
        // Esto significa que el programa esperara hasta que se cierre el modal antes de continuar
        modalStage.showAndWait();
    }

    /**
     *
     */

    public Parent cargarVista(String rutaFxml) {
        // Creamos un nodo raiz nulo
        Parent root = null;

        try {
            // Cargamos la vista FXML segun su ruta y devuelve el nodo raiz
            root = CargadorDeVistas.cargarVista(rutaFxml);

            // Verificamos que el nodo raiz sea diferente de nulo
            Objects.requireNonNull(root, "Un nodo FXML raíz no debe ser nulo");

        } catch (Exception e) {
            cerrar("No se puede cargar la vista FXML: " + rutaFxml, e);
        }
        return root;
    }

    public void setTitulo(String titulo) {
        // Establecemos un título a la ventana principal
        if (stagePrincipal != null) {
            stagePrincipal.setTitle(titulo);
        }
    }

    private void cerrar(String errorMsj, Exception e) {
        // Registramos el error utilizando el Logger
        log.error(errorMsj, e, e.getCause());
        // Cerramos de la aplicacion
        Platform.exit();
    }
}
