package com.tubiblioteca.controller.ABMAutor;

// Importaciones necesarias para el controlador y sus dependencias
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Autor;
import com.tubiblioteca.service.Autor.AutorServicio;
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

public class FormularioAutorControlador implements Initializable {

    // Elementos de la interfaz de usuario que se inyectan desde el archivo FXML
    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    // Logger para registrar eventos y errores
    private final Logger log = LoggerFactory.getLogger(FormularioAutorControlador.class);

    // Variable para almacenar el autor que se está editando o agregando
    private Autor autorInicial;

    // Lista observable para almacenar los nuevos autores agregados
    private ObservableList<Autor> nuevosAutores = FXCollections.observableArrayList();

    // Servicio que proporciona las operaciones CRUD para los autores
    private AutorServicio servicio;

    // Método llamado al inicializar el controlador
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa el servicio de autor con el repositorio obtenido de la configuración de la aplicación
        servicio = new AutorServicio(AppConfig.getRepositorio());

        // Establece el autor inicial como null y limpia la lista de nuevos autores
        autorInicial = null;
        nuevosAutores.clear();

        // Configura un atajo de teclado para el botón de guardar (usando la tecla Enter)
        ControlUI.configurarAtajoTecladoEnter(btnGuardar);
    }

    // Método llamado cuando se presiona el botón "Nuevo"
    @FXML
    private void nuevo() {
        // Limpia el campo de texto para permitir la entrada de un nuevo nombre de autor
        txtNombre.clear();
    }

    // Método llamado cuando se presiona el botón "Guardar"
    @FXML
    private void guardar() {
        try {
            // Obtiene el texto del campo de texto y lo limpia de espacios en blanco
            String nombre = txtNombre.getText().trim();

            // Si no hay un autor inicial (es decir, estamos agregando un nuevo autor)
            if (autorInicial == null) {
                // Valida e inserta el nuevo autor, luego lo agrega a la lista de nuevos autores
                nuevosAutores.add(servicio.validarEInsertar(nombre));
                // Muestra un mensaje de éxito
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el autor correctamente!");
            } else {
                // Si hay un autor inicial (es decir, estamos modificando un autor existente)
                servicio.validarYModificar(autorInicial, nombre);
                // Muestra un mensaje de éxito
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el autor correctamente!");
                // Cierra el modal actual
                StageManager.cerrarModal(Vista.FormularioAutor);
            }
        } catch (Exception e) {
            // En caso de error, registra el error y muestra un mensaje de error
            log.error("Error al guardar el autor.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el autor.\n" + e.getMessage());
        }
    }

    // Método para autocompletar el campo de texto con el nombre del autor inicial
    private void autocompletar() {
        txtNombre.setText(autorInicial.getNombre());
    }

    // Método para establecer el autor inicial y preparar la vista para la edición
    public void setAutorInicial(Autor autor) {
        this.autorInicial = autor;
        if (autor != null) {
            // Si se proporciona un autor, autocompleta el campo de texto y desactiva el botón "Nuevo"
            autocompletar();
            btnNuevo.setDisable(true);
        }
    }

    // Método para obtener la lista de nuevos autores agregados
    public List<Autor> getAutores() {
        return nuevosAutores;
    }
}
