package com.tubiblioteca.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "autor")
public class Prestamo {
    
    @Id
    @Column(name = "id", nullable =  false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo = LocalDate.now();
    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDate fechaDevolucion;
    @Column(name = "multa", nullable = false)
    private double multa = 0;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;
    @ManyToOne
    @JoinColumn(name = "dni_miembro", nullable = false)
    private Miembro miembro;
    @ManyToOne
    @JoinColumn(name = "id_copialibro", nullable = false)
    private CopiaLibro copiaLibro;
}
