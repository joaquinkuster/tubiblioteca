package com.tubiblioteca.controller.ABMLibro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.model.Autor;
import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.model.Editorial;
import com.tubiblioteca.model.Idioma;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.service.Autor.AutorServicio;
import com.tubiblioteca.service.Categoria.CategoriaServicio;
import com.tubiblioteca.service.Editorial.EditorialServicio;
import com.tubiblioteca.service.Idioma.IdiomaServicio;
import com.tubiblioteca.service.Libro.LibroServicio;
import com.tubiblioteca.view.Vista;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;

public class FormularioLibroControlador implements Initializable {

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

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnGuardar;

    private final ObservableList<Autor> autores = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private final ObservableList<Editorial> editoriales = FXCollections.observableArrayList();
    private final ObservableList<Idioma> idiomas = FXCollections.observableArrayList();

    private final Logger log = LoggerFactory.getLogger(FormularioLibroControlador.class);

    private Libro libroInicial;
    private ObservableList<Libro> nuevosLibros = FXCollections.observableArrayList();
    private LibroServicio servicio;
    private AutorServicio servicioAutor;
    private EditorialServicio servicioEditorial;
    private CategoriaServicio servicioCategoria;
    private IdiomaServicio servicioIdioma;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        var repositorio = AppConfig.getRepositorio();
        servicio = new LibroServicio(repositorio);
        servicioAutor = new AutorServicio(repositorio);
        servicioEditorial = new EditorialServicio(repositorio);
        servicioCategoria = new CategoriaServicio(repositorio);
        servicioIdioma = new IdiomaServicio(repositorio);

        inicializarCombosFormulario();

        libroInicial = null;
        nuevosLibros.clear();

        ControlUI.configurarAtajoTecladoEnter(btnGuardar);

        // listener para ver los cambios al seleccionar los autores

        cmbAutores.getCheckModel().getCheckedItems().addListener((ListChangeListener<Autor>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    List<Autor> seleccionados = new ArrayList<>(cmbAutores.getCheckModel().getCheckedItems());
                }
            }
        });


    }

    private void inicializarCombosFormulario() {
        autores.clear();
        autores.addAll(servicioAutor.buscarTodos());
        cmbAutores.getItems().addAll(autores);

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

    @FXML
    private void nuevo() {
        txtIsbn.clear();
        txtTitulo.clear();
        cmbCategoria.setValue(null);
        cmbEditorial.setValue(null);
        cmbIdioma.setValue(null);
        cmbAutores.getCheckModel().clearChecks();
    }

    @FXML
    private void guardar() {
        try {

            String isbn = txtIsbn.getText().trim();
            String titulo = txtTitulo.getText().trim();
            Categoria categoria = cmbCategoria.getValue();
            Editorial editorial = cmbEditorial.getValue();
            Idioma idioma = cmbIdioma.getValue();
            List<Autor> autores = new ArrayList<>(cmbAutores.getCheckModel().getCheckedItems());

            if (libroInicial == null) {
                nuevosLibros.add(servicio.validarEInsertar(isbn, titulo, categoria, editorial, idioma, autores));
                Alerta.mostrarMensaje(false, "Info", "Se ha agregado el libro correctamente!");
            } else {
                servicio.validarYModificar(libroInicial, titulo, categoria, editorial, idioma, autores);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el libro correctamente!");
                StageManager.cerrarModal(Vista.FormularioLibro);
            }
        } catch (Exception e) {
            log.error("Error al guardar el libro.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el libro. " + e.getMessage());
        }
    }

    private void autocompletar() {
        txtIsbn.setText(String.valueOf(libroInicial.getIsbn()));
        txtTitulo.setText(libroInicial.getTitulo());
        cmbCategoria.setValue(libroInicial.getCategoria());
        cmbEditorial.setValue(libroInicial.getEditorial());
        cmbIdioma.setValue(libroInicial.getIdioma());
        // Para seleccionar los autores primero limpiamos los autores seleccionados
        cmbAutores.getCheckModel().clearChecks();
        // Creamos una lista de los autores que tiene el libro
        List<Autor> autoresDelLibro = libroInicial.getAutores();
        // recorremos la lista para ir seleccionando las opciones segun los autores que
        // tenga el libro
        for (Autor autor : autoresDelLibro) {
            cmbAutores.getCheckModel().check(autor);
        }
    }

    public void setLibroInicial(Libro libro) {
        this.libroInicial = libro;
        if (libro != null) {
            autocompletar();
            txtIsbn.setDisable(true);
            btnNuevo.setDisable(true);
        }
    }

    public List<Libro> getLibros() {
        return nuevosLibros;
    }
}
