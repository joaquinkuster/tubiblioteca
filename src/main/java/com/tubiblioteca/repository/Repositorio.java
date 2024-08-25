package com.tubiblioteca.repository;

import java.util.List;

import com.tubiblioteca.auditoria.AuditoriaListener;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;

/**
 *
 * Implementación Genérica de un repositorio usando un EntityManager. 
 * 
 * Se usa el mismo repositorio visto en clases en el ejemplo sin usar FXML.
 * 
 */

public class Repositorio {
    
    private final EntityManager em;
        
    public Repositorio(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();      
    }
    
    public void iniciarTransaccion() {
        em.getTransaction().begin();
    }
    
    public void confirmarTransaccion() {
        em.getTransaction().commit();
    }

    public void descartarTransaccion() {
        em.getTransaction().rollback();
    }
    
    public void insertar(Object o) {
        this.em.persist(o);
    }
    
    public void modificar(Object o){
        this.em.merge(o);
    }

    public void eliminar(Object o){
        this.em.remove(o);
    }

    public void refrescar(Object o) {
        this.em.refresh(o);
    }
    
    // Metodo generico
    // Acepta cualquier tipo (T) que extienda de Object
    // Devuelve un objeto de tipo (T)    
    public <T extends Object> T buscar(Class<T> clase, Object id) {
        return (T) this.em.find(clase, id);
    }    
    
    // Metodo generico
    // Acepta cualquier tipo (T) que extienda de Object
    // Devuelve una lista de ese tipo (T)
    public <T extends Object> List<T> buscarTodos (Class<T> clase) {
        // obtengo un CriteriaBuilder
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        // creo un CriteriaQuery
        CriteriaQuery<T> consulta = cb.createQuery(clase);
        // defino el FROM
        Root<T> origen = consulta.from(clase);
        // defino el select (opcional)
        consulta.select(origen);
        // ejecuto y obtengo el resultado
        return em.createQuery(consulta).getResultList();      
    }

    // el parametro de orden a pasar se obtiene del metamodelo generado por EclipseLink
    public <T extends Object, P extends Object> List<T> buscarTodosOrdenadosPor (Class<T> clase, SingularAttribute<T, P> orden) {
        // obtengo un CriteriaBuilder
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        // creo un CriteriaQuery
        CriteriaQuery<T> consulta = cb.createQuery(clase);
        // Defino el FROM
        Root<T> origen = consulta.from(clase); 
        // defino el select (opcional)
        consulta.select(origen);
        // ordenado de forma ascendente (cb.asc() ) 
        // el atributo de origen definido en orden
        consulta.orderBy(cb.asc(origen.get(orden)));
        // ejecuto y obtengo el resultado
        return em.createQuery(consulta).getResultList();
    }
}
