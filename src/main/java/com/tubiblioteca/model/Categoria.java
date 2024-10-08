package com.tubiblioteca.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tubiblioteca.auditoria.AuditoriaListener;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Validacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "categoria")
public class Categoria {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Libro> libros = new HashSet<>();

    public Categoria() {

    }

    public Categoria(String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese el nombre de la categoria.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre de la categoria no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El nombre de la categoria debe contener solo letras y espacios.");
        }
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese una categoria.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre de la categoria no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El nombre de la categoria debe contener solo letras y espacios.");
        }
        this.nombre = nombre;
    }

    public boolean isBaja() {
        return baja;
    }

    public void setBaja() {
        this.baja = true;
    }

    public Set<Libro> getLibros() {
        return libros;
    }

    public void agregarLibro(Libro libro) {
        libros.add(libro);
    }

    public void quitarLibro(Libro libro) {
        libros.remove(libro);
    }

    public String toString() {
        return ControlUI.limitar(nombre, 15);
    }
}
