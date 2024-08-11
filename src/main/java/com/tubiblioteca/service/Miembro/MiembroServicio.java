package com.tubiblioteca.service.Miembro;

import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class MiembroServicio extends CrudServicio<Miembro> {

    public MiembroServicio(Repositorio repositorio) {
        super(repositorio, Miembro.class);
    }

    @Override
    protected boolean esInactivo(Miembro miembro) {
        return miembro.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Miembro miembro) {
        miembro.setBaja();
    }
}