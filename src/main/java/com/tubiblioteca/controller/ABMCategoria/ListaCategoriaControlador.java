package com.tubiblioteca.controller.ABMCategoria;

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
import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.service.Categoria.CategoriaServicio;
import com.tubiblioteca.view.Vista;
import com.tubiblioteca.helper.Alerta;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListaCategoriaControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Categoria, String> colNombre;

    // Tabla de categorías
    @FXML
    private TableView<Categoria> tblCategorias;

    // Campo de texto para el filtro de nombre
    @FXML
    private TextField txtNombre;
    
    // Listas utilizadas para almacenar las categorías
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private final ObservableList<Categoria> filtrados = FXCollections.observableArrayList();
    
    // Logger para gestionar información y errores
    private final Logger log = LoggerFactory.getLogger(ListaCategoriaControlador.class);

    // Servicio para manejar las operaciones de categorías
    private CategoriaServicio servicio;

    // Par para manejar el formulario de categoría
    private Pair<FormularioCategoriaControlador, Parent> formulario;

    // Método llamado al inicializar el controlador
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
    }

    // Método para inicializar la tabla con las categorías
    private void inicializarTabla() {
        try {
            // Inicializa el servicio de categoría con el repositorio obtenido de la configuración de la aplicación
            servicio = new CategoriaServicio(AppConfig.getRepositorio());

            // Configura la columna de nombre en la tabla
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            // Limpia las listas y carga todas las categorías desde el servicio
            categorias.clear();
            filtrados.clear();
            categorias.addAll(servicio.buscarTodos());
            filtrados.addAll(categorias);
            tblCategorias.setItems(filtrados);
        } catch (Exception e) {
            // Registra un error si ocurre un problema al inicializar la lista de categorías
            log.error("Error al inicializar la lista de categorias: ", e);
        }
    }

    // Método llamado cuando se presiona el botón "Modificar"
    @FXML
    private void modificar() {
        Categoria categoria = tblCategorias.getSelectionModel().getSelectedItem();

        // Muestra un mensaje de error si no se selecciona una categoría
        if (categoria == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una categoría!");
        } else {
            try {
                // Abre el formulario para modificar la categoría seleccionada
                abrirFormulario(categoria);

                // Si la categoría no cumple con el filtro, la elimina de la lista filtrada
                if (categoria != null && quitarFiltro(categoria)) {
                    filtrados.remove(categoria);
                }
                tblCategorias.refresh();
            } catch (Exception e) {
                // Registra un error si ocurre un problema al modificar la categoría
                log.error("Error al modificar la categoria: ", e);
            }
        }
    }

    // Método llamado cuando se presiona el botón "Agregar"
    @FXML
    private void agregar() {
        try {
            // Abre el formulario para agregar nuevas categorías
            List<Categoria> nuevasCategorias = abrirFormulario(null);
            if (nuevasCategorias != null) {
                // Añade cada nueva categoría a la lista de categorías y aplica el filtro
                for (Categoria categoria : nuevasCategorias) {
                    categorias.add(categoria);
                    if (aplicarFiltro(categoria)) {
                        filtrados.add(categoria);
                    }
                }
                tblCategorias.refresh();
            }
        } catch (Exception e) {
            // Registra un error si ocurre un problema al agregar una categoría
            log.error("Error al agregar una categoría: ", e);
        }
    }

    // Método llamado cuando se presiona el botón "Eliminar"
    @FXML
    private void eliminar() {
        Categoria categoria = tblCategorias.getSelectionModel().getSelectedItem();

        // Muestra un mensaje de error si no se selecciona una categoría
        if (categoria == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una categoría!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la categoría?")) {
            try {
                // Valida y borra la categoría seleccionada
                servicio.validarYBorrar(categoria);
                categorias.remove(categoria);
                filtrados.remove(categoria);
                Alerta.mostrarMensaje(false, "Info", "Categoría eliminada correctamente!");
                tblCategorias.refresh();
            } catch (Exception e) {
                // Registra un error si ocurre un problema al eliminar la categoría
                log.error("Error al eliminar la categoria.");
                Alerta.mostrarMensaje(true, "Error", e.getMessage());
            }
        }
    }

    // Método para abrir el formulario de categoría y manejar la categoría inicial
    private List<Categoria> abrirFormulario(Categoria categoriaInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioCategoria.getRutaFxml());
        FormularioCategoriaControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        // Si se proporciona una categoría inicial, se establece en el controlador del formulario
        if (categoriaInicial != null) {
            controladorFormulario.setCategoriaInicial(categoriaInicial);
        }

        // Abre el modal del formulario
        StageManager.abrirModal(vistaFormulario, Vista.FormularioCategoria);
        return controladorFormulario.getCategorias();
    }

    // Método llamado para aplicar el filtro a las categorías en la tabla
    @FXML
    private void filtrar() {
        filtrados.clear();
        categorias.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblCategorias.refresh();
    }

    // Método para aplicar el filtro basado en el nombre de la categoría
    private boolean aplicarFiltro(Categoria categoria) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre == null || categoria.toString().toLowerCase().contains(nombre));
    }

    // Método para verificar si una categoría debe ser eliminada del filtro
    private boolean quitarFiltro(Categoria categoria) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre != null && !categoria.toString().toLowerCase().contains(nombre));
    }

    // Método llamado cuando se presiona el botón "Limpiar Filtros"
    @FXML
    private void limpiarFiltros() {
        txtNombre.clear();
        filtrar();
    }
}
