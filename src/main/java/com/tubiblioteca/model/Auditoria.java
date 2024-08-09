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
    @Column(name = "descripcion", length = 500, nullable = false)
    private String descripcion;
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "dni_miembro", nullable = false)
    private Miembro miembro;
}
