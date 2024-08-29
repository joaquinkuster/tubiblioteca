package com.tubiblioteca.controller.ABMCategoria;

// Importaciones necesarias para el controlador y sus dependencias
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.service.Categoria.CategoriaServicio;
import com.tubiblioteca.view.Vista;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FormularioCategoriaControlador implements Initializable {

    // Campo de texto para el nombre de la categoría
    @FXML
    private TextField txtNombre;

    // Botón para crear una nueva categoría
    @FXML
    private Button btnNuevo;

    // Botón para guardar la categoría
    @FXML
    private Button btnGuardar;

    // Logger para registrar eventos y errores
    private final Logger log = LoggerFactory.getLogger(FormularioCategoriaControlador.class);

    // Categoría inicial que se edita en el formulario
    private Categoria categoriaInicial;

    // Lista observable de nuevas categorías que se agregarán
    private ObservableList<Categoria> nuevasCategorias = FXCollections.observableArrayList();

    // Servicio para manejar las operaciones de categorías
    private CategoriaServicio servicio;

    // Método llamado al inicializar el controlador
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa el servicio de categoría con el repositorio obtenido de la configuración de la aplicación
        servicio = new CategoriaServicio(AppConfig.getRepositorio());

        // Inicializa la categoría inicial como nula y limpia la lista de nuevas categorías
        categoriaInicial = null;
        nuevasCategorias.clear();

        // Configura el atajo de teclado para el botón de guardar
        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    // Método llamado cuando se presiona el botón "Nuevo"
    @FXML
    private void nuevo() {
        // Limpia el campo de texto para ingresar una nueva categoría
        txtNombre.clear();
    }

    // Método llamado cuando se presiona el botón "Guardar"
    @FXML
    private void guardar() {
        try {
            // Obtiene el texto del campo de nombre y elimina espacios en blanco
            String nombre = txtNombre.getText().trim();

            // Si no hay una categoría inicial, se valida e inserta la nueva categoría
            if (categoriaInicial == null) {
                nuevasCategorias.add(servicio.validarEInsertar(nombre));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado la categoría correctamente!");
            } else {
                // Si hay una categoría inicial, se valida y modifica la categoría existente
                servicio.validarYModificar(categoriaInicial, nombre);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado la categoría correctamente!");
                // Cierra el modal del formulario de categoría
                StageManager.cerrarModal(Vista.FormularioCategoria);
            }
        } catch (Exception e) {
            // Registra un error si ocurre un problema al guardar la categoría
            log.error("Error al guardar la categoría.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar la categoría. \n" + e.getMessage());
        }
    }

    // Completa automáticamente el campo de texto con el nombre de la categoría inicial
    private void autocompletar() {
        txtNombre.setText(categoriaInicial.getNombre());
    }

    // Establece la categoría inicial en el controlador y autocompleta el campo de texto si la categoría no es nula
    public void setCategoriaInicial(Categoria categoria) {
        this.categoriaInicial = categoria;
        if (categoria != null) {
            autocompletar();
            // Desactiva el botón "Nuevo" si se está editando una categoría existente
            btnNuevo.setDisable(true);
        }
    }

    // Retorna la lista de nuevas categorías
    public List<Categoria> getCategorias() {
        return nuevasCategorias;
    }
}
