package com.tubiblioteca.controller.ABMLibro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tubiblioteca.config.AppConfig;
import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckComboBox;
public class FormularioLibroControlador implements Initializable {

    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtTitulo;
    @FXML
    private CheckComboBox<Autor> cmbAutores;
    @FXML
    private ComboBox<Categoria> cmbCategoria;
    @FXML
    private ComboBox<Editorial> cmbEditorial;
    @FXML
    private ComboBox<Idioma> cmbIdioma;

    @FXML
    private Button btnNuevo;

    private final ObservableList<Autor> autores = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private final ObservableList<Editorial> editoriales = FXCollections.observableArrayList();
    private final ObservableList<Idioma> idiomas = FXCollections.observableArrayList();

    private final Logger log = LoggerFactory.getLogger(FormularioLibroControlador.class);

    private Libro libro;
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
        libro = null;
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
    }

    @FXML
    private void guardar() {
        try {
            // Creamos un nuevo libro auxiliar
            Libro aux = new Libro(
                    txtIsbn.getText().trim(),
                    txtTitulo.getText().trim(),
                    cmbCategoria.getValue(),
                    cmbEditorial.getValue(),
                    cmbIdioma.getValue(),
                    cmbAutores.getCheckModel().getCheckedItems());
            ArrayList<String> errores = new ArrayList<>();

            // Validamos si la categoria, editorial o idioma seleccionados existen
            if (servicioCategoria.buscarPorId(aux.getCategoria().getId()) == null) {
                errores.add("La categoría seleccionada no se encuentra en la base de datos.");
            }
            if (servicioEditorial.buscarPorId(aux.getEditorial().getId()) == null) {
                errores.add("La editorial seleccionada no se encuentra en la base de datos.");
            }
            if (servicioIdioma.buscarPorId(aux.getIdioma().getId()) == null) {
                errores.add("El idioma seleccionado no se encuentra en la base de datos.");
            }
            for (Autor autor : aux.getAutores()) {
                if (servicioAutor.buscarPorId(autor.getId()) == null) {
                    errores.add("El autor "  + autor + " no se encuentra en la base de datos.");
                }
            }

            if (!errores.isEmpty()) {
                throw new IllegalArgumentException(Alerta.convertirCadenaErrores(errores));
            }

            if (libro == null) {
                // Validamos si el ISBN ya está en uso
                if (servicio.buscarPorId(aux.getIsbn()) == null) {
                    libro = aux;
                    servicio.insertar(aux);
                    Alerta.mostrarMensaje(false, "Info", "Se ha agregado el libro correctamente!");
                } else {
                    throw new IllegalArgumentException(
                            "El ISBN ingresado ya está en uso. Por favor, ingrese otro ISBN.");
                }
            } else {
                // Actualizamos el libro existente
                libro.setTitulo(aux.getTitulo());
                libro.setCategoria(aux.getCategoria());
                libro.setEditorial(aux.getEditorial());
                libro.setIdioma(aux.getIdioma());
                libro.setAutores(aux.getAutores());

                servicio.modificar(libro);
                Alerta.mostrarMensaje(false, "Info", "Se ha modificado el libro correctamente!");
            }

            StageManager.cerrarModal(Vista.FormularioLibro);
        } catch (Exception e) {
            log.error("Error al guardar el libro.");
            Alerta.mostrarMensaje(true, "Error", "No se pudo guardar el libro. " + e.getMessage());
        }
    }

    private void autocompletar() {
        txtIsbn.setText(String.valueOf(libro.getIsbn()));
        txtTitulo.setText(libro.getTitulo());
        cmbCategoria.setValue(libro.getCategoria());
        cmbEditorial.setValue(libro.getEditorial());
        cmbIdioma.setValue(libro.getIdioma());
        
        // Para seleccionar los autores primero limpiamos los autores seleccionados
        cmbAutores.getCheckModel().clearChecks();
        // Creamos una lista de los autores que tiene el libro
        List<Autor> autoresDelLibro = libro.getAutores();
        // recorremos la lista para ir seleccionando las opciones segun los autores que tenga el libro
        for (Autor autor : autoresDelLibro) {
            cmbAutores.getCheckModel().check(autor);
        }
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
        if (libro != null) {
            autocompletar();
            txtIsbn.setDisable(true);
            btnNuevo.setDisable(true);
        }
    }

    public Libro getLibro() {
        return libro;
    }
}
