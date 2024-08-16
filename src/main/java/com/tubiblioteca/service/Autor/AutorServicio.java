package com.tubiblioteca.service.Autor;

import com.tubiblioteca.model.Autor;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class AutorServicio extends CrudServicio<Autor> {

    public AutorServicio(Repositorio repositorio) {
        super(repositorio, Autor.class);
    }

    @Override
    protected boolean esInactivo(Autor autor) {
        return autor.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Autor autor) {
        autor.setBaja();
    }
}