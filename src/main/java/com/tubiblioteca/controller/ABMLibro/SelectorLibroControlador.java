package com.tubiblioteca.controller.ABMLibro;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.model.Autor;
import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.model.Editorial;
import com.tubiblioteca.model.Idioma;
import com.tubiblioteca.service.Autor.AutorServicio;
import com.tubiblioteca.service.Categoria.CategoriaServicio;
import com.tubiblioteca.service.Editorial.EditorialServicio;
import com.tubiblioteca.service.Idioma.IdiomaServicio;
import com.tubiblioteca.service.Libro.LibroServicio;
import com.tubiblioteca.view.Vista;

import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectorLibroControlador implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Libro, Long> colIsbn;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutores;
    @FXML
    private TableColumn<Libro, Categoria> colCategoria;
    @FXML
    private TableColumn<Libro, Editorial> colEditorial;
    @FXML
    private TableColumn<Libro, Idioma> colIdioma;
    @FXML
    private TableColumn<Libro, Void> colVerificarCopias;

    // Tabla de libros
    @FXML
    private TableView<Libro> tblLibros;

    // Filtros adicionales
    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtTitulo;
    @FXML
    private CheckComboBox<Autor> cmbAutores;
    @FXML
    private SearchableComboBox<Categoria> cmbCategoria;
    @FXML
    private SearchableComboBox<Editorial> cmbEditorial;
    @FXML
    private SearchableComboBox<Idioma> cmbIdioma;

    // Listas utilizadas
    private final ObservableList<Libro> libros = FXCollections.observableArrayList();
    private final ObservableList<Libro> filtrados = FXCollections.observableArrayList();
    private final ObservableList<Autor> autores = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private final ObservableList<Editorial> editoriales = FXCollections.observableArrayList();
    private final ObservableList<Idioma> idiomas = FXCollections.observableArrayList();

    // Logger para gestionar información
    private final Logger log = LoggerFactory.getLogger(ListaLibrosControlador.class);

    private LibroServicio servicio;
    private AutorServicio servicioAutor;
    private EditorialServicio servicioEditorial;
    private CategoriaServicio servicioCategoria;
    private IdiomaServicio servicioIdioma;

    private Libro libro;

    @FXML
    private Button btnConfirmar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTabla();
        inicializarFiltros();
        ControlUI.configurarAtajoTecladoEnter(btnConfirmar);

    }

    private void inicializarTabla() {
        try {
            var repositorio = AppConfig.getRepositorio();
            servicio = new LibroServicio(repositorio);
            servicioAutor = new AutorServicio(repositorio);
            servicioEditorial = new EditorialServicio(repositorio);
            servicioCategoria = new CategoriaServicio(repositorio);
            servicioIdioma = new IdiomaServicio(repositorio);

            colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
            colAutores.setCellValueFactory(cellData -> {
                Libro libro = cellData.getValue();
                ObservableList<Autor> autores = FXCollections.observableArrayList(libro.getAutores());
                return new SimpleStringProperty(
                        autores.stream().map(Autor::toString).collect(Collectors.joining(", ")));
            });
            colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
            colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
            colIdioma.setCellValueFactory(new PropertyValueFactory<>("idioma"));

            libros.clear();
            filtrados.clear();
            libros.addAll(servicio.buscarTodos());
            filtrados.addAll(libros);
            tblLibros.setItems(filtrados);

        } catch (Exception e) {
            log.error("Error al inicializar la lista de libros: ", e);
        }
    }

    private void inicializarFiltros() {
        autores.clear();
        autores.addAll(servicioAutor.buscarTodos());
        cmbAutores.getItems().addAll(autores);
        configurarListenerComboAutores();

        editoriales.clear();
        editoriales.addAll(servicioEditorial.buscarTodos());
        cmbEditorial.setItems(editoriales);

        categorias.clear();
        categorias.addAll(servicioCategoria.buscarTodos());
        cmbCategoria.setItems(categorias);

        idiomas.clear();
        idiomas.addAll(servicioIdioma.buscarTodos());
        cmbIdioma.setItems(idiomas);
    }

    private void configurarListenerComboAutores() {
        // Listener para detectar cambios en los autores seleccionados
        cmbAutores.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Autor>() {
            @Override
            public void onChanged(Change<? extends Autor> change) {
                while (change.next()) {
                    if (change.wasAdded() || change.wasRemoved()) {
                        List<Autor> seleccionados = new ArrayList<>(cmbAutores.getCheckModel().getCheckedItems());
                        filtrar();
                    }
                }
            }
        });
    }

    @FXML
    private void confirmar() {
        // Obtenemos el libro seleccionado
        Libro libro = tblLibros.getSelectionModel().getSelectedItem();

        // Verificamos que sea diferente de nulo
        if (libro == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un libro!");
        } else {
            this.libro = libro;
            // Salimos del modal
            StageManager.cerrarModal(Vista.SelectorLibro);
        }
    }

    public void setLibro(Libro libro) {
        if (libro != null) {
            tblLibros.getSelectionModel().select(libro);
        }
    }

    public Libro getLibro() {
        return libro;
    }

    @FXML
    private void filtrar() {
        filtrados.clear();
        libros.stream()
                .filter(this::aplicarFiltro)
                .forEach(filtrados::add);
        tblLibros.refresh();
    }

    private boolean aplicarFiltro(Libro libro) {
        String isbn = txtIsbn.getText().trim().toLowerCase();
        String titulo = txtTitulo.getText().trim().toLowerCase();
        Categoria categoria = cmbCategoria.getValue();
        Editorial editorial = cmbEditorial.getValue();
        Idioma idioma = cmbIdioma.getValue();
        return filtrarPorAutores(libro.getAutores())
                && (isbn == null || String.valueOf(libro.getIsbn()).toLowerCase().startsWith(isbn))
                && (titulo == null || libro.getTitulo().toLowerCase().contains(titulo))
                && (categoria == null || categoria.equals(libro.getCategoria()))
                && (editorial == null || editorial.equals(libro.getEditorial()))
                && (idioma == null || idioma.equals(libro.getIdioma()));
    }

    private boolean filtrarPorAutores(Set<Autor> autores) {
        List<Autor> seleccionados = new ArrayList<>(cmbAutores.getCheckModel().getCheckedItems());
        if (seleccionados == null || seleccionados.isEmpty()) {
            return true;
        } else {
            for (Autor seleccionado : seleccionados) {
                if (!autores.contains(seleccionado)) {
                    return false;
                }
            }
        }
        return true;
    }

    @FXML
    private void limpiarFiltros() {
        txtIsbn.clear();
        txtTitulo.clear();
        cmbCategoria.setValue(null);
        cmbEditorial.setValue(null);
        cmbIdioma.setValue(null);
        cmbAutores.getCheckModel().clearChecks();
        filtrar();
    }

    @FXML
    private void verificarCopias() {
        Libro libro = tblLibros.getSelectionModel().getSelectedItem();

        if (libro == null) {
            Alerta.mostrarMensaje(true, "Error", "Debes seleccionar un libro!");
        } else {

            try{
                String respuesta = servicio.verificarCopias(libro);
                Alerta.mostrarMensaje(false, "Info", respuesta);
            } catch (Exception e) {
                Alerta.mostrarMensaje(true, "Error", e.getMessage());
            }
        }
    }
}