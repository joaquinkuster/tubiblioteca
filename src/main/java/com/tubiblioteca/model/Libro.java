package com.tubiblioteca.model;

import java.util.ArrayList;
import java.util.List;

import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Validacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @Column(name = "isbn", nullable = false)
    private long isbn;
    @Column(name = "titulo", length = 50, nullable = false)
    private String titulo;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
    @ManyToOne
    @JoinColumn(name = "id_editorial", nullable = false)
    private Editorial editorial;
    @ManyToOne
    @JoinColumn(name = "id_idioma", nullable = false)
    private Idioma idioma;
    @ManyToMany
    @JoinTable(name = "autoreslibros", joinColumns = @JoinColumn(name = "id_libro"), inverseJoinColumns = @JoinColumn(name = "id_autor"))
    private List<Autor> autores;

    public Libro() {

    }

    public Libro(String isbn, String titulo, Categoria categoria, Editorial editorial, Idioma idioma,
            List<Autor> autores) {

        ArrayList<String> errores = new ArrayList<>();

        // Verificamos que el ISBN no este vacio, que solo contenga digitos        
        if (isbn.isEmpty()) {
            errores.add("Por favor, ingrese un DNI.");
        } else if (Validacion.validarIsbn(String.valueOf(isbn))) {
            errores.add("El ISBN-13 debe contener exactamente 13 dígitos numéricos y comenzar con '978' o '979'.");
        }

        // Verificamos que el titulo no este vacio y que no supere los 50 caracteres
        if (titulo.isEmpty()) {
            errores.add("Por favor, ingrese un título.");
        } else if (titulo.length() > 50) {
            errores.add("El título no puede tener más de 50 caracteres.");
        } 

        // Verificamos que haya seleccionado una categoria
        if (categoria == null) {
            errores.add("Por favor, seleccione una categoría.");
        }
        
        // Verificamos que haya seleccionado una editorial
        if (editorial == null) {
            errores.add("Por favor, seleccione una editorial.");
        }

        // Verificamos que haya seleccionado un idioma
        if (idioma == null) {
            errores.add("Por favor, seleccione un idioma.");
        }

        // Verificamos que haya seleccionado un o mas autores
        if (autores.isEmpty()) {
            errores.add("Por favor, seleccione uno o más autores.");
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        this.isbn = Long.parseLong(isbn);
        this.titulo = titulo;
        this.categoria = categoria;
        this.editorial = editorial;
        this.idioma = idioma;
        this.autores = autores;
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un DNI.");
        } else if (Validacion.validarIsbn(String.valueOf(isbn))) {
            throw new IllegalArgumentException("El ISBN-13 debe contener exactamente 13 dígitos numéricos y comenzar con '978' o '979'.");
        }
        this.isbn = Long.parseLong(isbn);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un título.");
        } else if (titulo.length() > 50) {
            throw new IllegalArgumentException("El título no puede tener más de 50 caracteres.");
        } 
        this.titulo = titulo;
    }

    public boolean isBaja() {
        return baja;
    }

    public void setBaja() {
        this.baja = true;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Por favor, seleccione una categoría.");
        }
        this.categoria = categoria;
    }

    public Editorial getEditorial() {
        return editorial;
    }

    public void setEditorial(Editorial editorial) {
        if (editorial == null) {
            throw new IllegalArgumentException("Por favor, seleccione una editorial.");
        }
        this.editorial = editorial;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        if (idioma == null) {
            throw new IllegalArgumentException("Por favor, seleccione un idioma.");
        }
        this.idioma = idioma;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        if (autores.isEmpty()) {
            throw new IllegalArgumentException("Por favor, seleccione uno o más autores.");
        }
        this.autores = autores;
    }

    public String toString() {
        return ControlUI.limitar(titulo, 15);
    }
}
