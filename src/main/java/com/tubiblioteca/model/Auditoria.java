package com.tubiblioteca.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria")
public class Auditoria {
    
    @Id
    @Column(name = "id", nullable =  false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "tabla_afectada", length = 50, nullable = false)
    private String tablaAfectada;
    @Column(name = "accion", nullable = false)
    private TipoAccion accion;
    @Column(name = "descripcion", length = 500, nullable = false)
    private String descripcion;
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "dni_miembro", nullable = false)
    private Miembro miembro;
    
    public Auditoria() {

    }

    public Auditoria(String tablaAfectada, TipoAccion accion, String descripcion, LocalDateTime fechaHora, Miembro miembro) {
        this.tablaAfectada = tablaAfectada;
        this.accion = accion;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.miembro = miembro;
    }
    
    public String getTablaAfectada() {
        return tablaAfectada;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    public Miembro getMiembro() {
        return miembro;
    }
    public TipoAccion getAccion() {
        return accion;
    }

    public void setAccion(TipoAccion accion) {
        this.accion = accion;
    }
    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;
    }
}
