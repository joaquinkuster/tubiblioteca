package com.tubiblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "copalibro")
public class CopiaLibro {
    
    @Id
    @Column(name = "id", nullable =  false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "referencia", nullable = false)
    private boolean referencia;
    @Column(name = "tipo", nullable = false)
    private TipoCopiaLibro tipo;
    @Column(name = "estado", nullable = false)
    private EstadoCopiaLibro estado;
    @Column(name = "precio", nullable = false)
    private double precio;
    @ManyToOne
    @JoinColumn(name = "id_rack", nullable = false)
    private Rack rack;
    
}
