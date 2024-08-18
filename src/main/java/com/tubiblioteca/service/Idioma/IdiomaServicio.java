package com.tubiblioteca.service.Idioma;

import com.tubiblioteca.model.Idioma;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class IdiomaServicio extends CrudServicio<Idioma> {

    public IdiomaServicio(Repositorio repositorio) {
        super(repositorio, Idioma.class);
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