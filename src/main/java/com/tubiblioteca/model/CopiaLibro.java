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
    @Column(name = "id", nullable =  false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "referencia", nullable = false)
    private boolean referencia = false;
    @Column(name = "tipo", nullable = false)
    private TipoCopiaLibro tipo;
    @Column(name = "estado", nullable = false)
    private EstadoCopiaLibro estado = EstadoCopiaLibro.Disponible;
    @Column(name = "precio", nullable = false)
    private double precio;
    @ManyToOne
    @JoinColumn(name = "id_rack", nullable = false)
    private Rack rack;
    @ManyToOne
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    public CopiaLibro(){

    }

    public CopiaLibro(TipoCopiaLibro tipo, double precio, Rack rack, Libro libro) {
        this.tipo = tipo;
        this.precio = precio;
        this.rack = rack;
        this.libro = libro;
    }

    public int getId(){
        return id;
    }

    public boolean isReferencia() {
        return referencia;
    }

    public void setReferencia() {
        this.referencia = true;
    }

    public TipoCopiaLibro getTipo() {
        return tipo;
    }

    public void setTipo(TipoCopiaLibro tipo) {
        this.tipo = tipo;
    }

    public EstadoCopiaLibro getEstado() {
        return estado;
    }

    public void setEstado(EstadoCopiaLibro estado) {
        this.estado = estado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public String toString() {
        return String.format("%s %s", id, tipo);
    }    
}
