package com.tubiblioteca.model;

import java.util.List;

import com.tubiblioteca.auditoria.AuditoriaListener;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Validacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "autor")
public class Autor {
    
    @Id
    @Column(name = "id", nullable =  false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;
    @ManyToMany(mappedBy = "autores")
    private List<Libro> libros;

    public Autor(){

    }

    public Autor (String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese el nombre del autor.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre del autor no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El nombre del autor debe contener solo letras y espacios.");
        }
        this.nombre = nombre;
    }

    public int getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un nombre de autor.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre del autor no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El nombre del autor debe contener solo letras y espacios.");
        }
        this.nombre = nombre;
    }

    public boolean isBaja() {
        return baja;
    }

    public void setBaja() {
        this.baja = true;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    @Override
    public String toString() {
        return ControlUI.limitar(nombre, 15);
    }
}
