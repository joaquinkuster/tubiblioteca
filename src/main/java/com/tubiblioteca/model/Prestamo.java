package com.tubiblioteca.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tubiblioteca.auditoria.AuditoriaListener;
import com.tubiblioteca.helper.Fecha;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;
    @Column(name = "fecha_devolucion", nullable = true)
    private LocalDate fechaDevolucion = null;
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

        List<String> errores = new ArrayList<>();

        // Verificamos que la fecha de prestamo no este vacia y que no sea anterior al
        // día de hoy
        if (fechaPrestamo == null) {
            errores.add("Por favor, ingrese una fecha de préstamo.");
        } else if (fechaPrestamo.isBefore(LocalDate.now())) {
            errores.add("Debe seleccionar una fecha de préstamo que sea igual o posterior al dia de hoy "
                    + Fecha.fechaHoy() + ".");
        }

        // Verificamos que haya seleccionado un miembro
        if (miembro == null) {
            errores.add("Por favor, seleccione un miembro de la biblioteca.");
        }

        // Verificamos que haya seleccionado una copia
        if (copiaLibro == null) {
            errores.add("Por favor, seleccione una copia de libro.");
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        this.fechaPrestamo = fechaPrestamo;
        this.miembro = miembro;
        this.copiaLibro = copiaLibro;
    }

    public int getId() {
        return id;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        if (fechaPrestamo == null) {
            throw new IllegalArgumentException("Por favor, ingrese una fecha de préstamo.");
        } else if (fechaPrestamo.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha de préstamo que sea igual o posterior al dia de hoy " + Fecha.fechaHoy()
                            + ".");
        }
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        if (fechaDevolucion == null) {
            throw new IllegalArgumentException("Por favor, ingrese una fecha de devolución.");
        } else if (fechaDevolucion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha de devolución que sea igual o anterior al dia de hoy "
                            + Fecha.fechaHoy() + ".");
        } else if (this.fechaPrestamo != null) {
            if (fechaDevolucion.isBefore(fechaPrestamo)) {
                throw new IllegalArgumentException(
                        "La fecha de devolución debe ser posterior o igual a la fecha de préstamo.");
            }
        }
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
        if (miembro == null) {
            throw new IllegalArgumentException("Por favor, seleccione un miembro de la biblioteca.");
        }
        this.miembro = miembro;
    }

    public CopiaLibro getCopiaLibro() {
        return copiaLibro;
    }

    public void setCopiaLibro(CopiaLibro copiaLibro) {
        if (miembro == null) {
            throw new IllegalArgumentException("Por favor, seleccione un miembro de la biblioteca.");
        }
        this.copiaLibro = copiaLibro;
    }

    @Override
    public String toString() {
        return "Préstamo [fechaPrestamo=" + fechaPrestamo + ", fechaDevolucion=" + fechaDevolucion + ", multa=" + multa
                + ", miembro=" + miembro + ", copiaLibro=" + copiaLibro + "]";
    }
}
