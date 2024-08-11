package com.tubiblioteca.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class CargadorDeVistas {

    public static Parent cargarVista(String rutaFxml) throws IOException {
        // Preparamos el loader y le pasamos la ruta del archivo FXML
        FXMLLoader loader = crearLoader(rutaFxml);
        // Cargamos el archivo FXML segun su ruta
        // Devolvemos el nodo raiz del arbol de todos
        return loader.load();
    }

    public static FXMLLoader crearLoader(String rutaFxml){
        // Creamos un nuevo loader
        FXMLLoader loader = new FXMLLoader();

        // Configuramos el loader 
        loader.setLocation(CargadorDeVistas.class.getResource(rutaFxml));

        // Devolvemos el loader configurado
        return loader;
    }
}