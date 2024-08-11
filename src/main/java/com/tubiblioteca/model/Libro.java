package com.tubiblioteca.model;

import java.util.List;

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
    @JoinTable(
        name = "autoreslibros",
        joinColumns = @JoinColumn(name = "id_libro"),
        inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    private List<Autor> autores;
}
