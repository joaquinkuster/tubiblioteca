package com.tubiblioteca.model;

import java.util.List;

import com.tubiblioteca.auditoria.AuditoriaListener;
import com.tubiblioteca.helper.ControlUI;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "rack")
public class Rack {
    
    @Id
    @Column(name = "id", nullable =  false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "descripcion", length = 500, nullable = false)
    private String descripcion;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;
    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CopiaLibro> copiasLibros;

    public Rack(){

    }

    public Rack (String descripcion) {
        if (descripcion.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese una descripción del rack.");
        } else if (descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción del rack no puede tener más de 500 caracteres.");
        } 
        this.descripcion = descripcion;
    }

    public int getId(){
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese una descripción del rack.");
        } else if (descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción del rack no puede tener más de 500 caracteres.");
        } 
        this.descripcion = descripcion;
    }


    public boolean isBaja() {
        return baja;
    }

    public void setBaja() {
        this.baja = true;
    }

    public List<CopiaLibro> getCopiasLibros() {
        return copiasLibros;
    }

    public String toString() {
        return ControlUI.limitar(descripcion, 15);
    }
}
