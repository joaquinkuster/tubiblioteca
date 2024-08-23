package com.tubiblioteca.model;

import java.util.List;

import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Validacion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "editorial")
public class Editorial {
    
    @Id
    @Column(name = "id", nullable =  false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;
    @OneToMany(mappedBy = "editorial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Libro> libros;

    public Editorial(){

    }

    public Editorial (String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un nombre de la editorial.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre de la editorial no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El nombre de la editorial debe contener solo letras y espacios.");
        }
        this.nombre = nombre;
    }

    public int getId(){
        return id;
    }

    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un nombre de la editorial.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre de la editorial no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El nombre de la editorial debe contener solo letras y espacios.");
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

    public String toString() {
        return ControlUI.limitar(nombre, 15);
    }
}
