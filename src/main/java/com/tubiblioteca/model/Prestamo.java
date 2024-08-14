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
    @Column(name = "id", nullable =  false)
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

    public Prestamo(){

    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public double getMulta() {
        return multa;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }

    public boolean isBaja() {
        return baja;
    }

    public void setBaja() {
        this.baja = true;
    }

    public Miembro getMiembro() {
        return miembro;
    }

    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;
    }

    public CopiaLibro getCopiaLibro() {
        return copiaLibro;
    }

    public void setCopiaLibro(CopiaLibro copiaLibro) {
        this.copiaLibro = copiaLibro;
    }

    
}
