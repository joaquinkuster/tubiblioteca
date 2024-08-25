package com.tubiblioteca.auditoria;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PostRemove;

import java.time.LocalDateTime;

import com.tubiblioteca.model.Auditoria;
import com.tubiblioteca.security.SesionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
public class AuditoriaListener {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("TuBibliotecaPU");

    @PreUpdate
    public void preUpdate(Object entidad) {
        guardarAuditoria(entidad, "modificacion");
    }

    @PrePersist
    public void prePersist(Object entidad) {
        guardarAuditoria(entidad, "alta");
    }

    @PostRemove
    public void postRemove(Object entidad) {
        guardarAuditoria(entidad, "baja");
    }

    private void guardarAuditoria(Object entidad, String accion) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Auditoria auditoria = new Auditoria();
            auditoria.setTablaAfectada(entidad.getClass().getSimpleName());
            auditoria.setAccion(accion);
            auditoria.setFechaHora(LocalDateTime.now());
            auditoria.setDatoAfectado(entidad.toString());
            auditoria.setMiembro(SesionManager.getMiembro());

            em.persist(auditoria);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
