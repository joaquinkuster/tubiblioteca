package com.tubiblioteca.service.Editorial;

import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.model.Editorial;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class EditorialServicio extends CrudServicio<Editorial> {

    public EditorialServicio(Repositorio repositorio) {
        super(repositorio, Editorial.class);
    }

    @Override
    public Editorial validarEInsertar(Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];

        try {
            Editorial nuevaEditorial = new Editorial(nombre);
            insertar(nuevaEditorial);
            return nuevaEditorial;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void validarYModificar(Editorial editorial, Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];
        Editorial aux = new Editorial();

        try {
            aux.setNombre(nombre);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        editorial.setNombre(nombre);
        modificar(editorial);
    }

    @Override
    public void validarYBorrar(Editorial editorial) {

        for (Libro libro : editorial.getLibros()) {
            if (!libro.isBaja()) {
                throw new IllegalArgumentException("No se puede eliminar la editorial porque tiene libros asociados.");
            }
        }

        borrar(editorial);
    }

    @Override
    protected boolean esInactivo(Editorial editorial) {
        return editorial.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Editorial editorial) {
        editorial.setBaja();
    }

    public void agregarQuitarLibro(Editorial editorialNueva, Libro libro) {
        try {
            Editorial editorialVieja = libro.getEditorial();
            editorialNueva.agregarLibro(libro);
            modificar(editorialNueva);
            if (!editorialNueva.equals(editorialVieja)) {
                editorialVieja.quitarLibro(libro);
                modificar(editorialVieja);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}