package com.tubiblioteca.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Implementación Genérica de un repositorio usando un EntityManager. 
 *  
 */

public class Repositorio {
    
    private final EntityManager em;  // Instancia del EntityManager para gestionar las operaciones con la base de datos
        
    public Repositorio(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();  // Se crea un EntityManager a partir del EntityManagerFactory
    }
    
    // Inicia una transacción en la base de datos
    public void iniciarTransaccion() {
        em.getTransaction().begin();
    }
    
    // Confirma (commits) la transacción en curso
    public void confirmarTransaccion() {
        em.getTransaction().commit();
    }

    // Descartar (rollback) la transacción en curso en caso de error o necesidad de deshacer cambios
    public void descartarTransaccion() {
        em.getTransaction().rollback();
    }
    
    // Inserta un objeto en la base de datos
    public void insertar(Object o) {
        this.em.persist(o);  // Persistir el objeto en la base de datos
    }
    
    // Modifica un objeto en la base de datos
    public void modificar(Object o) {
        this.em.merge(o);  // Fusionar el objeto modificado con la base de datos
    }

    // Elimina un objeto de la base de datos
    public void eliminar(Object o) {
        this.em.remove(o);  // Eliminar el objeto de la base de datos
    }

    // Refresca el estado del objeto en la base de datos
    public void refrescar(Object o) {
        this.em.refresh(o);  // Actualizar el estado del objeto desde la base de datos
    }
    
    // Método genérico para buscar un objeto por su clase y su identificador
    // Acepta cualquier tipo (T) que extienda de Object
    // Devuelve un objeto de tipo (T)    
    public <T extends Object> T buscar(Class<T> clase, Object id) {
        return (T) this.em.find(clase, id);  // Buscar el objeto por su identificador y clase
    }    
    
    // Método genérico para buscar todos los objetos de una clase
    // Acepta cualquier tipo (T) que extienda de Object
    // Devuelve una lista de ese tipo (T)
    public <T extends Object> List<T> buscarTodos(Class<T> clase) {
        // Obtengo un CriteriaBuilder desde el EntityManager
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        // Creo una CriteriaQuery para la clase especificada
        CriteriaQuery<T> consulta = cb.createQuery(clase);
        // Defino el FROM de la consulta (la entidad desde la cual se consultará)
        Root<T> origen = consulta.from(clase);
        // Defino el SELECT de la consulta (opcional, ya que por defecto selecciona el FROM)
        consulta.select(origen);
        // Ejecuto la consulta y obtengo el resultado como una lista de objetos
        return em.createQuery(consulta).getResultList();      
    }
}
