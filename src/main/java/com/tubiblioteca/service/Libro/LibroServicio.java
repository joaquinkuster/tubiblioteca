package com.tubiblioteca.service.Libro;

import java.util.ArrayList;
import java.util.List;
import com.tubiblioteca.model.Autor;
import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.model.Editorial;
import com.tubiblioteca.model.Idioma;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;
import com.tubiblioteca.service.Autor.AutorServicio;
import com.tubiblioteca.service.Categoria.CategoriaServicio;
import com.tubiblioteca.service.Editorial.EditorialServicio;
import com.tubiblioteca.service.Idioma.IdiomaServicio;

public class LibroServicio extends CrudServicio<Libro> {

    // Servicios utilizados para las validaciones
    private CategoriaServicio servicioCategoria;
    private EditorialServicio servicioEditorial;
    private IdiomaServicio servicioIdioma;
    private AutorServicio servicioAutor;

    public LibroServicio(Repositorio repositorio) {
        super(repositorio, Libro.class);
        servicioCategoria = new CategoriaServicio(repositorio);
        servicioEditorial = new EditorialServicio(repositorio);
        servicioIdioma = new IdiomaServicio(repositorio);
        servicioAutor = new AutorServicio(repositorio);
    }

    @Override
    public Libro validarEInsertar(Object... datos) {
        if (datos.length != 6) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String isbn = (String) datos[0];
        String titulo = (String) datos[1];
        Categoria categoria = (Categoria) datos[2];
        Editorial editorial = (Editorial) datos[3];
        Idioma idioma = (Idioma) datos[4];
        @SuppressWarnings("unchecked")
        List<Autor> autores = (List<Autor>) datos[5];

        List<String> errores = new ArrayList<>();

        // Validamos si el ISBN ya está en uso
        if (isbn.matches("\\d+")) {
            if (buscarPorId(Long.parseLong(isbn)) != null) {
                errores.add("El ISBN ingresado ya está en uso. Por favor, ingrese otro ISBN.");
            }
        }

        // Validamos si el miembro o la copia seleccionados existen
        if (categoria != null) {
            if (!servicioCategoria.existe(categoria)) {
                errores.add("La categoría seleccionada no se encuentra en la base de datos.");
            }
        }

        if (editorial != null) {
            if (!servicioEditorial.existe(editorial)) {
                errores.add("La editorial seleccionada no se encuentra en la base de datos.");
            }
        }

        if (idioma != null) {
            if (!servicioIdioma.existe(idioma)) {
                errores.add("El idioma seleccionado no se encuentra en la base de datos.");
            }
        }

        for (Autor autor : autores) {
            if (autor != null) {
                if (!servicioAutor.existe(autor)) {
                    errores.add("El autor " + autor + " no se encuentra en la base de datos.");
                }
            }

        }

        try {
            new Libro(isbn, titulo, categoria, editorial, idioma, autores);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        Libro libro = new Libro(isbn, titulo, categoria, editorial, idioma, autores);
        insertar(libro);
        return libro;
    }

    @Override
    public void validarYModificar(Libro libro, Object... datos) {
        if (datos.length != 5) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String titulo = (String) datos[0];
        Categoria categoria = (Categoria) datos[1];
        Editorial editorial = (Editorial) datos[2];
        Idioma idioma = (Idioma) datos[3];
        @SuppressWarnings("unchecked")
        List<Autor> autores = (List<Autor>) datos[4];
        Libro aux = new Libro();

        List<String> errores = new ArrayList<>();

        // Validamos si el miembro o la copia seleccionados existen
        if (categoria != null) {
            if (!servicioCategoria.existe(categoria)) {
                errores.add("La categoría seleccionada no se encuentra en la base de datos.");
            }
        }

        if (editorial != null) {
            if (!servicioEditorial.existe(editorial)) {
                errores.add("La editorial seleccionada no se encuentra en la base de datos.");
            }
        }

        if (idioma != null) {
            if (!servicioIdioma.existe(idioma)) {
                errores.add("El idioma seleccionado no se encuentra en la base de datos.");
            }
        }

        for (Autor autor : autores) {
            if (autor != null) {
                if (!servicioAutor.existe(autor)) {
                    errores.add("El autor " + autor + " no se encuentra en la base de datos.");
                }
            }

        }

        try {
            aux.setTitulo(titulo);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        try {
            aux.setCategoria(categoria);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        try {
            aux.setEditorial(editorial);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        try {
            aux.setIdioma(idioma);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        try {
            aux.setAutores(autores);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        libro.setTitulo(titulo);
        libro.setCategoria(categoria);
        libro.setEditorial(editorial);
        libro.setIdioma(idioma);
        libro.setAutores(autores);
        modificar(libro);
    }

    @Override
    protected boolean esInactivo(Libro libro) {
        return libro.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Libro libro) {
        libro.setBaja();
    }

    @Override
    public boolean existe(Libro libro) {
        return buscarPorId(libro.getIsbn()) != null
                && !libro.isBaja();
    }
}