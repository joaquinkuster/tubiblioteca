package com.tubiblioteca.service.Libro;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.tubiblioteca.model.Autor;
import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.model.CopiaLibro;
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
        Set<Autor> autores = (Set<Autor>) datos[5];

        List<String> errores = new ArrayList<>();

        // Validamos si el ISBN ya está en uso
        if (isbn.matches("\\d+")) {
            if (buscarPorId(Long.parseLong(isbn)) != null) {
                errores.add("El ISBN ingresado ya está en uso. Por favor, ingrese otro ISBN.");
            }
        }

        // Validamos si la categoria se encuentra en la base de datos
        if (categoria != null) {
            if (!servicioCategoria.existe(categoria, categoria.getId())) {
                errores.add("La categoría seleccionada no se encuentra en la base de datos.");
            }
        }

        // Validamos si la editorial se encuentra en la base de datos
        if (editorial != null) {
            if (!servicioEditorial.existe(editorial, editorial.getId())) {
                errores.add("La editorial seleccionada no se encuentra en la base de datos.");
            }
        }

        // Validamos si el idioma se encuentra en la base de datos
        if (idioma != null) {
            if (!servicioIdioma.existe(idioma, idioma.getId())) {
                errores.add("El idioma seleccionado no se encuentra en la base de datos.");
            }
        }

        // Validamos si los autores se encuentran en la base de datos
        for (Autor autor : autores) {
            if (autor != null) {
                if (!servicioAutor.existe(autor, autor.getId())) {
                    errores.add("El autor " + autor + " no se encuentra en la base de datos.");
                }
            }

        }

        Libro libro = new Libro();

        try {
            libro = new Libro(isbn, titulo, categoria, editorial, idioma, autores);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        insertar(libro);
        agregarLibroAEntidades(libro, autores, categoria, editorial, idioma);
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
        Set<Autor> autores = (Set<Autor>) datos[4];
        Libro aux = new Libro();

        List<String> errores = new ArrayList<>();

        // Validamos si el miembro o la copia seleccionados existen
        if (categoria != null) {
            if (!servicioCategoria.existe(categoria, categoria.getId())) {
                errores.add("La categoría seleccionada no se encuentra en la base de datos.");
            }
        }

        if (editorial != null) {
            if (!servicioEditorial.existe(editorial, editorial.getId())) {
                errores.add("La editorial seleccionada no se encuentra en la base de datos.");
            }
        }

        if (idioma != null) {
            if (!servicioIdioma.existe(idioma, idioma.getId())) {
                errores.add("El idioma seleccionado no se encuentra en la base de datos.");
            }
        }

        for (Autor autor : autores) {
            if (autor != null) {
                if (!servicioAutor.existe(autor, autor.getId())) {
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

        agregarLibroAEntidades(libro, autores, categoria, editorial, idioma);
        libro.setTitulo(titulo);
        libro.setCategoria(categoria);
        libro.setEditorial(editorial);
        libro.setIdioma(idioma);
        libro.setAutores(autores);
        modificar(libro);
    }

    @Override
    public void validarYBorrar(Libro libro) {

        for (CopiaLibro copia : libro.getCopias()) {
            if (!copia.isBaja()) {
                throw new IllegalArgumentException("No se puede eliminar el libro porque tiene copias asociadas.");
            }
        }

        borrar(libro);
    }

    @Override
    protected boolean esInactivo(Libro libro) {
        return libro.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Libro libro) {
        libro.setBaja();
    }

    public String verificarCopias(Libro libro) {
        List<CopiaLibro> copias = new ArrayList<>(libro.getCopias());

        if (copias.isEmpty()) {
            throw new IllegalArgumentException("El libro seleccionado no tiene ninguna copia disponible.");
        } else {
            String respuesta = "El libro: " + libro + " tiene las siguientes copias disponibles: \n";
            for (CopiaLibro copia : copias) {
                respuesta += "Tipo: " + copia.getTipo() + " | Precio: " + copia.getPrecio() + "\n";
            }
            return respuesta;
        }
    }

    public void agregarQuitarCopia(Libro libroNuevo, CopiaLibro copia) {
        try {
            Libro libroViejo = copia.getLibro();
            libroNuevo.agregarCopia(copia);
            modificar(libroNuevo);
            if (!libroNuevo.equals(libroViejo)) {
                libroViejo.quitarCopia(copia);
                modificar(libroViejo);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public void agregarAutor(Libro libro, Autor autor) {
        try {
            libro.agregarAutor(autor);
            modificar(libro);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private void agregarLibroAEntidades(Libro libro, Set<Autor> autoresNuevos, Categoria categoria,
            Editorial editorial,
            Idioma idioma) {
        Set<Autor> autoresViejos = libro.getAutores();
        for (Autor autor : autoresNuevos) {
            servicioAutor.agregarLibro(autor, libro);
        }
        if (!autoresNuevos.equals(autoresViejos)) {
            for (Autor autor : autoresViejos) {
                if (!autoresNuevos.contains(autor)) {
                    servicioAutor.quitarLibro(autor, libro);
                }
            }
        }
        servicioCategoria.agregarQuitarLibro(categoria, libro);
        servicioEditorial.agregarQuitarLibro(editorial, libro);
        servicioIdioma.agregarQuitarLibro(idioma, libro);
    }
}