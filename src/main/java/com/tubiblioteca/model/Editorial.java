package com.tubiblioteca.model;

import com.tubiblioteca.helper.Validacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public Editorial(){

    }

    public Editorial (String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un nombre de la editorial.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre de la editorial no puede tener más de 50 caracteres.");
        } else if (Validacion.validarCadena(nombre)) {
            throw new IllegalArgumentException("El nombre de la editorial debe contener solo letras y espacios.");
        }
        this.nombre = nombre;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un nombre de la editorial.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre de la editorial no puede tener más de 50 caracteres.");
        } else if (Validacion.validarCadena(nombre)) {
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

    public String toString() {
        return nombre;
    }
}
