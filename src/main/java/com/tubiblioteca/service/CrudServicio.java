package com.tubiblioteca.service;

import com.tubiblioteca.repository.Repositorio;
import java.util.ArrayList;
import java.util.List;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;

public abstract class CrudServicio<T> {
    
    // Logger para gestionar informacion
    private static final Logger log = getLogger(CrudServicio.class);

    private Repositorio repositorio;
    private Class<T> clase;

    public CrudServicio(Repositorio repositorio, Class<T> clase) {
        this.repositorio = repositorio;
        this.clase = clase;
    }

    public List<T> buscarTodos() {
        var todos = this.repositorio.buscarTodos(clase);
        var activos = new ArrayList<T>();
        for (var entidad : todos) {
            if (!esInactivo(entidad)) {
                activos.add(entidad);
            }
        }
        return activos;
    }

    public T buscarPorId(Object id) {
        var entidad = this.repositorio.buscar(clase, id);
        if (entidad != null) {
            if (!esInactivo(entidad)) {
                return entidad;
            }
        }
        return null;
    }

    public void insertar(T entidad) {
        try {
            this.repositorio.iniciarTransaccion();
            this.repositorio.insertar(entidad);
            this.repositorio.confirmarTransaccion();
        } catch (Exception e) {
            this.repositorio.descartarTransaccion();
            log.error("Error al insertar la entidad de tipo {}: {}", entidad.getClass().getSimpleName(), e, e.getCause());
            throw e;
        }
    }

    public void modificar(T entidad) {
        try {
            this.repositorio.iniciarTransaccion();
            this.repositorio.modificar(entidad);
            this.repositorio.confirmarTransaccion();
        } catch (Exception e) {
            this.repositorio.descartarTransaccion();
            log.error("Error al modificar la entidad de tipo {}: {}", entidad.getClass().getSimpleName(), e, e.getCause());
            throw e;
        }
    }

    public void borrar(T entidad) {
        try {
            this.repositorio.iniciarTransaccion();
            if (entidad != null && !esInactivo(entidad)) {
                marcarComoInactivo(entidad);
                this.repositorio.modificar(entidad);
                this.repositorio.confirmarTransaccion();
            } else {
                this.repositorio.descartarTransaccion();
            }
        } catch (Exception e) {
            this.repositorio.descartarTransaccion();
            log.error("Error al eliminar la entidad de tipo {}: {}", entidad.getClass().getSimpleName(), e, e.getCause());
            throw e;
        }
    }

    protected abstract boolean esInactivo(T entidad);

    protected abstract void marcarComoInactivo(T entidad);
}
