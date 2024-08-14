package com.tubiblioteca.controller;

import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.view.Vista;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import java.net.URL;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalControlador implements Initializable {

    // BorderPane del contenedor principal
    @FXML
    private BorderPane bpVistaPrincipal;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(PrincipalControlador.class);

    // Variable para almacenar el menú
    private Pair<MenuControlador, Parent> menu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarMenu();
    }

    private void configurarMenu(){
        // Cargar la vista con el controlador
        menu = StageManager.cargarVistaConControlador(Vista.Menu.getRutaFxml());

        // Validar que el menú no sea nulo
        if (menu != null) {
            // Configurar el panel en el controlador
            MenuControlador controladorMenu = menu.getKey();
            Parent vistaMenu = menu.getValue();
            
            if (controladorMenu != null && vistaMenu != null) {
                controladorMenu.setPanel(bpVistaPrincipal);
                bpVistaPrincipal.setLeft(vistaMenu);
            } else {
                log.error("Error: El controlador o la vista del menú son nulos.");
            }
        } else {
            log.error("Error: El menú no se pudo cargar.");
        }
    }
}
