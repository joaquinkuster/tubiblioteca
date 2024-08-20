package com.tubiblioteca.model;

import com.tubiblioteca.helper.Validacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "idioma")
public class Idioma {
    
    @Id
    @Column(name = "id", nullable =  false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;

    public Idioma(){

    }

    public Idioma(String nombre){
       if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese el nombre.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El nombre debe contener solo letras y espacios.");
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
            throw new IllegalArgumentException("Por favor, ingrese un idioma.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El idioma no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El idioma debe contener solo letras y espacios.");
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
