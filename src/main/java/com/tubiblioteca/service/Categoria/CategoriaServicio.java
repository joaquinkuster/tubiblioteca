package com.tubiblioteca.service.Categoria;

import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class CategoriaServicio extends CrudServicio<Categoria> {

    public CategoriaServicio(Repositorio repositorio) {
        super(repositorio, Categoria.class);
    }

    @Override
    protected boolean esInactivo(Categoria categoria) {
        return categoria.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Categoria categoria) {
        categoria.setBaja();
    }
}