package com.tubiblioteca.service.Autor;

import java.util.ArrayList;
import java.util.List;

import com.tubiblioteca.model.Autor;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class AutorServicio extends CrudServicio<Autor> {

    public AutorServicio(Repositorio repositorio) {
        super(repositorio, Autor.class);
    }

    @Override
    public Autor validarEInsertar(Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];

        try {
            Autor nuevoAutor = new Autor(nombre);
            insertar(nuevoAutor);
            return nuevoAutor;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void validarYModificar(Autor autor, Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];
        Autor aux = new Autor();

        try {
            aux.setNombre(nombre);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        autor.setNombre(nombre);
        modificar(autor);
    }

    @Override
    public void validarYBorrar(Autor autor) {

        for (Libro libro : autor.getLibros()) {
            if (!libro.isBaja()) {
                throw new IllegalArgumentException("No se puede eliminar el autor porque tiene libros asociados.");
            }
        }

        borrar(autor);
    }

    @Override
    protected boolean esInactivo(Autor autor) {
        return autor.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Autor autor) {
        autor.setBaja();
    }

    public void agregarLibro(Autor autor, Libro libro) {
        try {
            autor.agregarLibro(libro);
            modificar(autor);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public void quitarLibro(Autor autor, Libro libro) {
        try {
            autor.quitarLibro(libro);
            modificar(autor);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}