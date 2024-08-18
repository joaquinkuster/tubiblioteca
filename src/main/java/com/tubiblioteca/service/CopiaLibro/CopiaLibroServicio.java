package com.tubiblioteca.service.CopiaLibro;

import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.EstadoCopiaLibro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class CopiaLibroServicio extends CrudServicio<CopiaLibro> {

    public CopiaLibroServicio(Repositorio repositorio) {
        super(repositorio, CopiaLibro.class);
    }

    @Override
    protected boolean esInactivo(CopiaLibro copia) {
        return copia.getEstado() == EstadoCopiaLibro.Perdida;
    }

    @Override
    protected void marcarComoInactivo(CopiaLibro copia) {
        editorial.setBaja();
    }
}