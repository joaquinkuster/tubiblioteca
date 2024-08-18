package com.tubiblioteca.service.CopiaLibro;

import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class CopiaLibroServicio extends CrudServicio<CopiaLibro> {

    public CopiaLibroServicio(Repositorio repositorio) {
        super(repositorio, CopiaLibro.class);
    }

    @Override
    protected boolean esInactivo(CopiaLibro copia) {
        return copia.isBaja();
    }

    @Override
    protected void marcarComoInactivo(CopiaLibro copia) {
        copia.setBaja();
    }
}