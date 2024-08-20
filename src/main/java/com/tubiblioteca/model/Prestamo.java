package com.tubiblioteca.model;

import java.time.LocalDate;
import java.util.ArrayList;

import com.tubiblioteca.helper.Alerta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;
    @Column(name = "fecha_devolucion", nullable = true)
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

    public Prestamo() {

    }

    public Prestamo(LocalDate fechaPrestamo, Miembro miembro, CopiaLibro copiaLibro) {

        ArrayList<String> errores = new ArrayList<>();

        // Verificamos que la fecha de prestamo no este vacia
        if (fechaPrestamo == null) {
            errores.add("Por favor, ingrese una fecha de reunión.");
        }

        // Verificamos que haya seleccionado un miembro
        if (miembro == null) {
            errores.add("Por favor, seleccione un miembro de la biblioteca.");
        }

        // Verificamos que haya seleccionado una copia
        if (copiaLibro == null) {
            errores.add("Por favor, seleccione una copia de libro");
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(Alerta.convertirCadenaErrores(errores));
        }
        this.fechaPrestamo = fechaPrestamo;
        this.miembro = miembro;
        this.copiaLibro = copiaLibro;
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

    public void setMulta(String multa) {
        try {
            this.multa = Double.parseDouble(multa);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "La multa debe ser un valor numérico. Por favor, ingrese un monto válido.");
        }
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
