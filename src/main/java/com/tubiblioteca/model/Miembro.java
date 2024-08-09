package com.tubiblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "miembro")
public class Miembro {

    @Id
    @Column(name = "dni", length = 8, nullable = false)
    private String dni;
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;
    @Column(name = "apellido", length = 50, nullable = false)
    private String apellido;
    @Column(name = "tipo", nullable = false)
    private TipoMiembro tipo;
    @Column(name = "clave", length = 500, nullable = false)
    private String clave;
    @Column(name = "baja", nullable = false)
    private Boolean baja = false;
}
