package com.tubiblioteca.controller.ABMAutor;

// Importaciones necesarias para el controlador y sus dependencias
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.model.Autor;
import com.tubiblioteca.service.Autor.AutorServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListaAutoresControlador implements Initializable {

    // Columna de la tabla que muestra el nombre del autor
    @FXML
    private TableColumn<Autor, String> colNombre;

    // Tabla que muestra la lista de autores
    @FXML
    private TableView<Autor> tblAutores;

    // Campo de texto para buscar autores por nombre
    @FXML
    private TextField txtNombre;

    // Lista observable de autores y lista filtrada
    private final ObservableList<Autor> autores = FXCollections.observableArrayList();
    private final ObservableList<Autor> filtrados = FXCollections.observableArrayList();

    // Logger para registrar eventos y errores
    private final Logger log = LoggerFactory.getLogger(ListaAutoresControlador.class);

    // Servicio para manejar las operaciones de los autores
    private AutorServicio servicio;

    // Par que contiene el controlador y la vista del formulario de autores
    private Pair<FormularioAutorControlador, Parent> formulario;

    // Método llamado al inicializar el controlador
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa la tabla de autores
        inicializarTabla();
    }

    // Configura la tabla de autores
    private void inicializarTabla() {
        try {
            // Inicializa el servicio de autor con el repositorio obtenido de la configuración de la aplicación
            servicio = new AutorServicio(AppConfig.getRepositorio());

            // Configura la columna para mostrar el nombre del autor
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            // Limpia las listas y carga todos los autores en la lista de autores
            autores.clear();
            filtrados.clear();
            autores.addAll(servicio.buscarTodos());
            filtrados.addAll(autores);

            // Establece la lista filtrada como el ítem de la tabla
            tblAutores.setItems(filtrados);
        } catch (Exception e) {
            // Registra un error si ocurre un problema al inicializar la lista de autores
            log.error("Error al inicializar la lista de autores: ", e);
        }
    }

    // Método llamado cuando se presiona el botón "Modificar"
    @FXML
    private void modificar() {
        // Obtiene el autor seleccionado en la tabla
        Autor autor = tblAutores.getSelectionModel().getSelectedItem();

        // Si no se selecciona un autor, muestra un mensaje de error
        if (autor == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un autor!");
        } else {
            try {
                // Abre el formulario para modificar el autor seleccionado
                abrirFormulario(autor);
                // Si el autor está en la lista filtrada, lo elimina de la lista filtrada
                if (autor != null && quitarFiltro(autor)) {
                    filtrados.remove(autor);
                }
                // Refresca la tabla para mostrar los cambios
                tblAutores.refresh();
            } catch (Exception e) {
                // Registra un error si ocurre un problema al modificar el autor
                log.error("Error al modificar el autor: ", e);
            }
        }
    }

    // Método llamado cuando se presiona el botón "Agregar"
    @FXML
    private void agregar() {
        try {
            // Abre el formulario para agregar un nuevo autor
            List<Autor> nuevosAutores = abrirFormulario(null);
            if (nuevosAutores != null) {
                // Agrega los nuevos autores a la lista de autores y a la lista filtrada si aplican el filtro
                for (Autor autor : nuevosAutores) {
                    autores.add(autor);
                    if (aplicarFiltro(autor)) {
                        filtrados.add(autor);
                    }
                }
            }
            // Refresca la tabla para mostrar los nuevos autores
            tblAutores.refresh();
        } catch (Exception e) {
            // Registra un error si ocurre un problema al agregar un autor
            log.error("Error al agregar un autor: ", e);
        }
    }

    // Método llamado cuando se presiona el botón "Eliminar"
    @FXML
    private void eliminar() {
        // Obtiene el autor seleccionado en la tabla
        Autor autor = tblAutores.getSelectionModel().getSelectedItem();

        // Si no se selecciona un autor, muestra un mensaje de error
        if (autor == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un autor!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el autor?")) {
            try {
                // Valida y elimina el autor usando el servicio
                servicio.validarYBorrar(autor);
                // Elimina el autor de las listas de autores y filtrados
                autores.remove(autor);
                filtrados.remove(autor);
                // Muestra un mensaje de éxito
                Alerta.mostrarMensaje(false, "Info", "Autor eliminado correctamente!");
                // Refresca la tabla para mostrar los cambios
                tblAutores.refresh();
            } catch (Exception e) {
                // Registra un error si ocurre un problema al eliminar el autor
                log.error("Error al eliminar el autor.");
                Alerta.mostrarMensaje(true, "Error", e.getMessage());
            }
        }
    }

    // Método para abrir el formulario de autores y retornar los autores modificados
    private List<Autor> abrirFormulario(Autor autorInicial) throws IOException {
        // Carga la vista y el controlador del formulario de autores
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioAutor.getRutaFxml());
        FormularioAutorControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        // Si se proporciona un autor inicial, lo establece en el controlador del formulario
        if (autorInicial != null) {
            controladorFormulario.setAutorInicial(autorInicial);
        }

        // Abre el formulario como un modal
        StageManager.abrirModal(vistaFormulario, Vista.FormularioAutor);
        // Retorna la lista de autores del formulario
        return controladorFormulario.getAutores();
    }

    // Método llamado cuando se presiona el botón "Filtrar"
    @FXML
    private void filtrar() {
        // Limpia la lista de autores filtrados
        filtrados.clear();
        // Filtra los autores según el criterio de búsqueda y agrega los resultados a la lista filtrada
        autores.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        // Refresca la tabla para mostrar los resultados filtrados
        tblAutores.refresh();
    }

    // Método para determinar si un autor cumple con el criterio de búsqueda
    private boolean aplicarFiltro(Autor autor) {
        // Obtiene el texto del campo de búsqueda y lo convierte a minúsculas
        String nombre = txtNombre.getText().trim().toLowerCase();
        // Retorna true si el nombre del autor contiene el texto de búsqueda
        return (nombre == null || autor.toString().toLowerCase().contains(nombre));
    }

    // Método para determinar si un autor debe ser eliminado de los resultados filtrados
    private boolean quitarFiltro(Autor autor) {
        // Obtiene el texto del campo de búsqueda y lo convierte a minúsculas
        String nombre = txtNombre.getText().trim().toLowerCase();
        // Retorna true si el nombre del autor no contiene el texto de búsqueda
        return (nombre != null && !autor.toString().toLowerCase().contains(nombre));
    }

    // Método llamado cuando se presiona el botón "Limpiar Filtros"
    @FXML
    private void limpiarFiltros() {
        // Limpia el campo de búsqueda y aplica el filtro nuevamente
        txtNombre.clear();
        filtrar();
    }
}
