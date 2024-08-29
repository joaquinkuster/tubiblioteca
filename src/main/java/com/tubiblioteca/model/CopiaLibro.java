package com.tubiblioteca.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.tubiblioteca.auditoria.AuditoriaListener;
import com.tubiblioteca.helper.ControlUI;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@EntityListeners(AuditoriaListener.class)
@Table(name = "copalibro")
public class CopiaLibro {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "referencia", nullable = false)
    private boolean referencia;
    @Column(name = "tipo", nullable = false)
    private TipoCopiaLibro tipo;
    @Column(name = "estado", nullable = false)
    private EstadoCopiaLibro estado = EstadoCopiaLibro.Disponible;
    @Column(name = "precio", nullable = false)
    private double precio;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;
    @ManyToOne
    @JoinColumn(name = "id_rack", nullable = false)
    private Rack rack;
    @ManyToOne
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;
    @OneToMany(mappedBy = "copiaLibro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestamo> prestamos;

    public CopiaLibro() {

    }

    public CopiaLibro(TipoCopiaLibro tipo, String precio, Libro libro, Rack rack, boolean referencia) {

        List<String> errores = new ArrayList<>();

        // Verificamos que haya seleccionado un tipo
        if (tipo == null) {
            errores.add("Por favor, seleccione un tipo.");
        } else if (TipoCopiaLibro.valueOf(tipo.toString()) == null) {
            errores.add("El tipo seleccionado no se encuentra registrado en el sistema.");
        }

        // Verificamos que el precio sea un valor numerico
        try {
            Double.parseDouble(precio);
        } catch (NumberFormatException e) {
            errores.add("El precio debe ser un valor numérico. Por favor, ingrese un monto válido.");
        }

        // Verificamos que haya seleccionado un libro
        if (libro == null) {
            errores.add("Por favor, seleccione un libro.");
        }

        // Verificamos que haya seleccionado un rack
        if (rack == null) {
            errores.add("Por favor, seleccione un rack.");
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        this.tipo = tipo;
        this.precio = Double.parseDouble(precio);
        this.rack = rack;
        this.libro = libro;
        this.referencia = referencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CopiaLibro copia = (CopiaLibro) o;
        return id == copia.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public boolean isReferencia() {
        return referencia;
    }

    public void setReferencia(boolean referencia) {
        this.referencia = referencia;
    }

    public TipoCopiaLibro getTipo() {
        return tipo;
    }

    public void setTipo(TipoCopiaLibro tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Por favor, seleccione un tipo.");
        } else if (TipoCopiaLibro.valueOf(tipo.toString()) == null) {
            throw new IllegalArgumentException("El tipo seleccionado no se encuentra registrado en el sistema.");
        }
        this.tipo = tipo;
    }

    public EstadoCopiaLibro getEstado() {
        return estado;
    }


    public void setEstado(EstadoCopiaLibro estado) {
        if (estado == null) {
            throw new IllegalArgumentException("Por favor, seleccione un estado.");
        } else if (EstadoCopiaLibro.valueOf(estado.toString()) == null) {
            throw new IllegalArgumentException("El estado seleccionado no se encuentra registrado en el sistema.");
        }
        this.estado = estado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        try {
            this.precio = Double.parseDouble(precio);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El precio debe ser un valor numérico. Por favor, ingrese un monto válido.");
        }
    }

    public boolean isBaja() {
        return baja;
    }

    public void setBaja() {
        this.baja = true;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        if (rack == null) {
            throw new IllegalArgumentException("Por favor, seleccione un rack.");
        }
        this.rack = rack;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Por favor, seleccione un libro.");
        }
        this.libro = libro;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void agregarPrestamo(Prestamo prestamo) {
        if (!prestamos.contains(prestamo)) {
            prestamos.add(prestamo);
        }
    }

    public String toString() {
        return String.format("%s %s - $%s", ControlUI.limitar(libro.toString(), 10), tipo, precio);
    }
}
