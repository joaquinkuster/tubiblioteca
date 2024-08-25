package com.tubiblioteca.auditoria;

import com.tubiblioteca.model.Auditoria;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class AuditoriaServicio extends CrudServicio<Auditoria> {

    public AuditoriaServicio(Repositorio repositorio) {
        super(repositorio, Auditoria.class);
    }

    @Override
    public Auditoria validarEInsertar(Object... datos) {
        return null;
    }

    @Override
    public void validarYModificar(Auditoria entidad, Object... datos) {

    }

    @Override
    public void validarYBorrar(Auditoria entidad) {

    }

    @Override
    protected boolean esInactivo(Auditoria entidad) {
        return false;
    }

    @Override
    protected void marcarComoInactivo(Auditoria entidad) {

    }
    
}
