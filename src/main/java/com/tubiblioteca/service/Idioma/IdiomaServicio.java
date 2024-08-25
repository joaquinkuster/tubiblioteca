package com.tubiblioteca.service.Idioma;

import com.tubiblioteca.model.Idioma;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class IdiomaServicio extends CrudServicio<Idioma> {

    public IdiomaServicio(Repositorio repositorio) {
        super(repositorio, Idioma.class);
    }

    @Override
    public Idioma validarEInsertar(Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];

        try {
            Idioma nuevoIdioma = new Idioma(nombre);
            insertar(nuevoIdioma);
            return nuevoIdioma;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void validarYModificar(Idioma idioma, Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];
        Idioma aux = new Idioma();

        try {
            aux.setNombre(nombre);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        idioma.setNombre(nombre);
        modificar(idioma);
    }

    @Override
    public void validarYBorrar(Idioma idioma) {

        for (Libro libro : idioma.getLibros()) {
            if (!libro.isBaja()) {
                throw new IllegalArgumentException("No se puede eliminar el idioma porque tiene libros asociados.");
            }
        }

        borrar(idioma);
    }

    @Override
    protected boolean esInactivo(Idioma idioma) {
        return idioma.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Idioma idioma) {
        idioma.setBaja();
    }
}