package com.tubiblioteca.service;

import java.util.List;

import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.repository.Repositorio;

/*
 * Se usa solamente una clase de servicio dado que es un proyecto simple. 
 * En proyectos más grandes se pueden usar varias clases de servicios 
 * (lo mismo aplica a repositorios donde se suelen usar un repositorio por 
 * cada clase que maneja datos).
 * 
 * El uso de varios servicios y repositorios lo vamos a tratar en POO2 
 * 
 */

public class Servicio {
    
    private Repositorio repositorio;

    public Servicio(Repositorio p) {
        this.repositorio = p;
    }

    // Listados

    // Se obtienen todos los miembros
    public List<Miembro> listarMiembros() {
        return this.repositorio.buscarTodos(Miembro.class);
    }
}