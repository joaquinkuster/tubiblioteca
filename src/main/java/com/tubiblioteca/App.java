package com.tubiblioteca;

import java.io.IOException;

import jakarta.persistence.Persistence;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // carga la escena principal
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/Login.fxml"));
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    // carga un archivo FXML
    // retorna el FXMLLoader para poder acceder a los controladores
    // ver por ejemplo: void editar(ActionEvent event) en PedidosController.java
    public static FXMLLoader setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        scene.setRoot(fxmlLoader.load());
        return fxmlLoader;
    }

    public static void main(String[] args) {
        launch();
    }
}