package com.tubiblioteca.service.Categoria;

import com.tubiblioteca.model.Categoria;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class CategoriaServicio extends CrudServicio<Categoria> {

    public CategoriaServicio(Repositorio repositorio) {
        super(repositorio, Categoria.class);
    }

    @Override
    public Categoria validarEInsertar(Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];

        try {
            Categoria nuevaCategoria = new Categoria(nombre);
            insertar(nuevaCategoria);
            return nuevaCategoria;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void validarYModificar(Categoria categoria, Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];
        Categoria aux = new Categoria();

        try {
            aux.setNombre(nombre);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        categoria.setNombre(nombre);
        modificar(categoria);
    }

    @Override
    protected boolean esInactivo(Categoria categoria) {
        return categoria.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Categoria categoria) {
        categoria.setBaja();
    }

    @Override
    public boolean existe(Categoria categoria) {
        return buscarPorId(categoria.getId()) != null 
        && !categoria.isBaja();
    }
}