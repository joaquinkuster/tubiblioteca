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
    
    
    public Auditoria(String tablaAfectada, TipoAccion accion, String datoAfectado, LocalDateTime fechaHora, Miembro miembro) {
        this.tablaAfectada = tablaAfectada;
        this.accion = accion;
        this.datoAfectado = datoAfectado;
        this.fechaHora = fechaHora;
        this.miembro = miembro;
    }

    public Auditoria() {

    }

    @Id
    @Column(name = "id", nullable =  false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "tabla_afectada", length = 50, nullable = false)
    private String tablaAfectada;
    @Column(name = "accion", length = 50, nullable = false)
    private TipoAccion accion;
    @Column(name = "dato_afectado", length = 50, nullable = false)
    private String datoAfectado;
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "dni_miembro", nullable = false)
    private Miembro miembro;
    
    public String getTablaAfectada() {
        return tablaAfectada;
    }
    public String getDatoAfectado() {
        return datoAfectado;
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
    public void setDatoAfectado(String datoAfectado) {
        this.datoAfectado = datoAfectado;
    }
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    public void setMiembro(Miembro miembro) {
        this.miembro = miembro;
    }

}
