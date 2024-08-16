package com.tubiblioteca.service.Libro;

import com.tubiblioteca.model.Libro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class LibroServicio extends CrudServicio<Libro> {

    public LibroServicio(Repositorio repositorio) {
        super(repositorio, Libro.class);
    }

    @Override
    protected boolean esInactivo(Libro libro) {
        return libro.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Libro libro) {
        libro.setBaja();
    }
}