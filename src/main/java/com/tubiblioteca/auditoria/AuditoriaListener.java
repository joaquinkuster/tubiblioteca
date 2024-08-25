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

    @PreUpdate
    public void preUpdate(Object entidad) {
        boolean esBaja = verificarBaja(entidad);
        if (esBaja) {
            guardarAuditoria(entidad, TipoAccion.baja);
        } else {
            guardarAuditoria(entidad, TipoAccion.modificacion);
        }
    }

    public boolean verificarBaja(Object entidad){
        
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

    @PrePersist
    public void prePersist(Object entidad) {
        guardarAuditoria(entidad, TipoAccion.alta);
    }

    @PostRemove
    public void postRemove(Object entidad) {
        guardarAuditoria(entidad, TipoAccion.baja);
    }

    private void guardarAuditoria(Object entidad, TipoAccion accion) {
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
