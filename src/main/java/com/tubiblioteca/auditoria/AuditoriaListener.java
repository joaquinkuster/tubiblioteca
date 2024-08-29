package com.tubiblioteca.auditoria;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PostRemove;

import java.time.LocalDateTime;

import java.lang.reflect.Field;

import com.tubiblioteca.model.Auditoria;
import com.tubiblioteca.model.TipoAccion;
import com.tubiblioteca.security.SesionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class AuditoriaListener {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("TuBibliotecaPU");

    // Método que se ejecuta antes de una actualización de la entidad
    @PreUpdate
    public void preUpdate(Object entidad) {
        if (verificarBaja(entidad)) {
            guardarAuditoria(entidad, TipoAccion.Baja);  // Si el campo "baja" es verdadero, registra como baja
        } else {
            guardarAuditoria(entidad, TipoAccion.Modificacion);  // De lo contrario, registra como modificación
        }
    }

    // Verifica si el campo "baja" de la entidad es verdadero
    private boolean verificarBaja(Object entidad) {
        try {
            Field bajaField = entidad.getClass().getDeclaredField("baja");
            bajaField.setAccessible(true);
            boolean valorActual = (boolean) bajaField.get(entidad);
            System.out.println(valorActual);

            return valorActual;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método que se ejecuta antes de la inserción de una nueva entidad
    @PrePersist
    public void prePersist(Object entidad) {
        guardarAuditoria(entidad, TipoAccion.Alta);  // Registra la operación como alta
    }

    // Método que se ejecuta después de la eliminación de una entidad
    @PostRemove
    public void postRemove(Object entidad) {
        guardarAuditoria(entidad, TipoAccion.Baja);  // Registra la operación como baja
    }

    // Guarda un registro de auditoría en la base de datos
    private void guardarAuditoria(Object entidad, TipoAccion accion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Crea un objeto Auditoria y establece sus propiedades
            Auditoria auditoria = new Auditoria();
            auditoria.setTablaAfectada(entidad.getClass().getSimpleName());  // Nombre de la tabla afectada
            auditoria.setAccion(accion);  // Tipo de acción (alta, modificación, baja)
            auditoria.setFechaHora(LocalDateTime.now());  // Fecha y hora de la acción
            auditoria.setDescripcion(entidad.toString());  // Datos de la entidad afectados
            auditoria.setMiembro(SesionManager.getMiembro());  // Miembro responsable de la acción

            em.persist(auditoria);  // Persiste el registro de auditoría en la base de datos

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();  // Deshace la transacción si ocurre un error
            throw e;
        } finally {
            em.close();  // Cierra el EntityManager
        }
    }
}
