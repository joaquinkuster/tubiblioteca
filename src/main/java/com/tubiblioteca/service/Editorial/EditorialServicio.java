package com.tubiblioteca.service.Editorial;

import com.tubiblioteca.model.Editorial;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class EditorialServicio extends CrudServicio<Editorial> {

    public EditorialServicio(Repositorio repositorio) {
        super(repositorio, Editorial.class);
    }

    @Override
    protected boolean esInactivo(Editorial editorial) {
        return editorial.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Editorial editorial) {
        editorial.setBaja();
    }
}