package com.tubiblioteca.controller.ABMCategoria;

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

    // Tabla de editoriales
    @FXML
    private TableView<Categoria> tblCategorias;

    // Filtros adicionales
    @FXML
    private TextField txtNombre;
    
    // Listas utilizadas
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private final ObservableList<Categoria> filtrados = FXCollections.observableArrayList();
    
    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaCategoriaControlador.class);

    private CategoriaServicio servicio;
    private Pair<FormularioCategoriaControlador, Parent> formulario;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
    }

    private void inicializarTabla() {
        try {
            servicio = new CategoriaServicio(AppConfig.getRepositorio());

            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            categorias.clear();
            filtrados.clear();
            categorias.addAll(servicio.buscarTodos());
            filtrados.addAll(categorias);
            tblCategorias.setItems(filtrados);
        } catch (Exception e) {
            log.error("Error al inicializar la lista de categorias: ", e);
        }
    }

    @FXML
    private void modificar() {
        Categoria categoria = tblCategorias.getSelectionModel().getSelectedItem();

        if (categoria == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una categoria!");
        } else {
            try {
                abrirFormulario(categoria);
                if (categoria != null && quitarFiltro(categoria)) {
                    filtrados.remove(categoria);
                }
                tblCategorias.refresh();
            } catch (Exception e) {
                log.error("Error al modificar la categoria: ", e);
            }
        }
    }

    @FXML
    private void agregar() {
        try {
            List<Categoria> nuevasCategorias = abrirFormulario(null);
            if (nuevasCategorias != null) {
                for (Categoria categoria : nuevasCategorias) {
                    categorias.add(categoria);
                    if (aplicarFiltro(categoria)) {
                        filtrados.add(categoria);
                    }
                }
                tblCategorias.refresh();
            }
        } catch (Exception e) {
            log.error("Error al agregar una categoría: ", e);
        }
    }

    @FXML
    private void eliminar() {
        Categoria categoria = tblCategorias.getSelectionModel().getSelectedItem();

        if (categoria == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar una categoría!");
        } else if (Alerta.mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la categoría?")) {
            try {
                servicio.borrar(categoria);
                categorias.remove(categoria);
                filtrados.remove(categoria);
                Alerta.mostrarMensaje(false, "Info", "categoria eliminada correctamente!");
                tblCategorias.refresh();
            } catch (Exception e) {
                log.error("Error al eliminar la categoria: ", e);
                Alerta.mostrarMensaje(true, "Error",
                        "No se pudo eliminar la categoria. Puede estar vinculado a otros registros.");
            }
        }
    }

    private List<Categoria> abrirFormulario(Categoria categoriaInicial) throws IOException {
        formulario = StageManager.cargarVistaConControlador(Vista.FormularioCategoria.getRutaFxml());
        FormularioCategoriaControlador controladorFormulario = formulario.getKey();
        Parent vistaFormulario = formulario.getValue();

        if (categoriaInicial != null) {
            controladorFormulario.setCategoriaInicial(categoriaInicial);
        }

        StageManager.abrirModal(vistaFormulario, Vista.FormularioCategoria);
        return controladorFormulario.getCategorias();
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        categorias.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblCategorias.refresh();
    }

    private boolean aplicarFiltro(Categoria categoria) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre == null || categoria.toString().toLowerCase().contains(nombre));
    }

    private boolean quitarFiltro(Categoria categoria) {
        String nombre = txtNombre.getText().trim().toLowerCase();
        return (nombre != null && !categoria.toString().toLowerCase().contains(nombre));
    }

    @FXML
    private void limpiarFiltros() {
        txtNombre.clear();
        filtrar();
    }
}
