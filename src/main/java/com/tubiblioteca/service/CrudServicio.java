package com.tubiblioteca.service;

import com.tubiblioteca.repository.Repositorio;
import java.util.ArrayList;
import java.util.List;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;

public abstract class CrudServicio<T> {

    // Logger para gestionar información
    private static final Logger log = getLogger(CrudServicio.class);

    private Repositorio repositorio;  // Repositorio para realizar operaciones de persistencia
    private Class<T> clase;            // Clase de las entidades que maneja este servicio

    // Constructor que inicializa el repositorio y la clase de las entidades
    public CrudServicio(Repositorio repositorio, Class<T> clase) {
        this.repositorio = repositorio;
        this.clase = clase;
    }

    // Método para buscar todos los objetos activos
    public List<T> buscarTodos() {
        var todos = this.repositorio.buscarTodos(clase);  // Obtener todas las entidades de la clase
        var activos = new ArrayList<T>();  // Lista para almacenar solo las entidades activas
        for (var entidad : todos) {
            if (!esInactivo(entidad)) {
                activos.add(entidad);  // Añadir entidad activa a la lista
            }
        }
        return activos;  // Retornar la lista de entidades activas
    }

    // Método para buscar una entidad por su identificador
    public T buscarPorId(Object id) {
        var entidad = this.repositorio.buscar(clase, id);  // Buscar la entidad por ID
        if (entidad != null) {
            if (!esInactivo(entidad)) {
                return entidad;  // Retornar la entidad si es activa
            }
        }
        return null;  // Retornar null si la entidad es inactiva o no se encuentra
    }

    // Método para insertar una nueva entidad
    protected void insertar(T entidad) {
        try {
            this.repositorio.iniciarTransaccion();  // Iniciar una transacción
            this.repositorio.insertar(entidad);  // Insertar la entidad

            this.repositorio.confirmarTransaccion();  // Confirmar la transacción
        } catch (Exception e) {
            this.repositorio.descartarTransaccion();  // Descartar la transacción en caso de error
            log.error("Error al insertar la entidad de tipo {}: {}", entidad.getClass().getSimpleName(), e, e.getCause());
            throw e;  // Volver a lanzar la excepción después de hacer rollback
        }
    }

    // Método para modificar una entidad existente
    protected void modificar(T entidad) {
        try {
            this.repositorio.iniciarTransaccion();  // Iniciar una transacción
            if (entidad != null) {
                this.repositorio.modificar(entidad);  // Modificar la entidad
                this.repositorio.confirmarTransaccion();  // Confirmar la transacción
            }
        } catch (Exception e) {
            this.repositorio.descartarTransaccion();  // Descartar la transacción en caso de error
            log.error("Error al modificar la entidad de tipo {}: {}", entidad.getClass().getSimpleName(), e, e.getCause());
            throw e;  // Volver a lanzar la excepción después de hacer rollback
        }
    }

    // Método para borrar (o marcar como inactiva) una entidad
    protected void borrar(T entidad) {
        try {
            this.repositorio.iniciarTransaccion();  // Iniciar una transacción
            if (entidad != null) {
                marcarComoInactivo(entidad);  // Marcar la entidad como inactiva
                this.repositorio.modificar(entidad);  // Modificar la entidad (actualizar estado)
                this.repositorio.confirmarTransaccion();  // Confirmar la transacción
            } else {
                this.repositorio.descartarTransaccion();  // Descartar la transacción si la entidad es null
            }
        } catch (Exception e) {
            this.repositorio.descartarTransaccion();  // Descartar la transacción en caso de error
            log.error("Error al eliminar la entidad de tipo {}: {}", entidad.getClass().getSimpleName(), e, e.getCause());
            throw e;  // Volver a lanzar la excepción después de hacer rollback
        }
    }

    // Método para verificar si una entidad existe y está activa
    public boolean existe(T entidad, Object id) {
        return buscarPorId(id) != null && !esInactivo(entidad);
    }

    // Métodos abstractos que deben ser implementados en las subclases
    public abstract T validarEInsertar(Object... datos);

    public abstract void validarYModificar(T entidad, Object... datos);

    public abstract void validarYBorrar(T entidad);

    protected abstract boolean esInactivo(T entidad);

    protected abstract void marcarComoInactivo(T entidad);
}
