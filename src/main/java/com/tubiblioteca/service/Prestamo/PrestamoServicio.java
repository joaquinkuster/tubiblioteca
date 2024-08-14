package com.tubiblioteca.service.Prestamo;

import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Prestamo;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class PrestamoServicio extends CrudServicio<Prestamo> {

    public PrestamoServicio(Repositorio repositorio) {
        super(repositorio, Prestamo.class);
    }

    @Override
    protected boolean esInactivo(Prestamo prestamo) {
        return prestamo.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Prestamo prestamo) {
        prestamo.setBaja();
    }
}