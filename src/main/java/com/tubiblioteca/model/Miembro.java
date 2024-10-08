package com.tubiblioteca.model;

import java.util.ArrayList;
import java.util.List;

import com.tubiblioteca.auditoria.AuditoriaListener;
import com.tubiblioteca.helper.ControlUI;
import com.tubiblioteca.helper.Validacion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@EntityListeners(AuditoriaListener.class)
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
    @OneToMany(mappedBy = "miembro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestamo> prestamos;

    public Miembro(){
        
    }

    public Miembro(String dni, String nombre, String apellido, TipoMiembro tipo, String clave) {

        List<String> errores = new ArrayList<>();

        // Verificamos que el DNI no este vacio, que solo contenga digitos        
        if (dni.isEmpty()) {
            errores.add("Por favor, ingrese un DNI.");
        } else if (Validacion.validarDni(dni)) {
            errores.add("El DNI debe contener 8 dígitos o una letra mayúscula M o F seguida de 7 dígitos numéricos.");
        } 

        // Verificamos que el nombre no este vacio y que no supere los 50 caracteres
        if (nombre.isEmpty()) {
            errores.add("Por favor, ingrese un nombre.");
        } else if (nombre.length() > 50) {
            errores.add("El nombre no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            errores.add("El nombre debe contener solo letras y espacios.");
        } 

        // Verificamos que el apellido no este vacio y que no supere los 50 caracteres
        if (apellido.isEmpty()) {
            errores.add("Por favor, ingrese un apellido.");
        } else if (apellido.length() > 50) {
            errores.add("El apellido no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(apellido)) {
            errores.add("El apellido debe contener solo letras y espacios.");
        } 

        // Verificamos que haya seleccionado un tipo
        if (tipo == null) {
            errores.add("Por favor, seleccione un tipo.");
        } else if (TipoMiembro.valueOf(tipo.toString()) == null) {
                errores.add("El tipo seleccionado no se encuentra registrado en el sistema.");
        } 

        // Verificamos que la clave no este vacia, no supere los 50 caracteres y cumpla con los requisitos
        if (clave.isEmpty()) {
            errores.add("Por favor, ingrese un contraseña.");
        } else if (clave.length() > 50) {
            errores.add("La contraseña no puede tener más de 50 caracteres.");
        } else if (Validacion.validarContrasena(clave)) {
            errores.add("Por favor, introduce una contraseña válida que cumpla con los siguientes requisitos:\n" +
            "  - Al menos 6 caracteres de longitud.\n" +
            "  - Al menos una letra mayúscula.\n" +
            "  - Al menos una letra minúscula.\n" +
            "  - Al menos un dígito numérico.\n" +
            "  - Al menos un carácter especial [@#$%^&+=!].");
        } 

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido; 
        this.tipo = tipo;
        this.clave = clave;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un DNI.");
        } else if (Validacion.validarDni(dni)) {
            throw new IllegalArgumentException("El DNI debe contener 8 dígitos o una letra mayúscula M o F seguida de 7 dígitos numéricos.");
        } 
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un nombre.");
        } else if (nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(nombre)) {
            throw new IllegalArgumentException("El nombre debe contener solo letras y espacios.");
        }
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un apellido.");
        } else if (apellido.length() > 50) {
            throw new IllegalArgumentException("El apellido no puede tener más de 50 caracteres.");
        } else if (Validacion.validarNombre(apellido)) {
            throw new IllegalArgumentException("El apellido debe contener solo letras y espacios.");
        }
        this.apellido = apellido;
    }

    public TipoMiembro getTipo() {
        return tipo;
    }

    public void setTipo(TipoMiembro tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Por favor, seleccione un tipo.");
        } else if (TipoMiembro.valueOf(tipo.toString()) == null) {
            throw new IllegalArgumentException("El tipo seleccionado no se encuentra registrado en el sistema.");
        }
        this.tipo = tipo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave, boolean estaCodificada) {
        if (clave.isEmpty()) {
            throw new IllegalArgumentException("Por favor, ingrese un contraseña.");
        } else if (!estaCodificada) {
            if (clave.length() > 50) {
                throw new IllegalArgumentException("La contraseña no puede tener más de 50 caracteres.");
            } else if (Validacion.validarContrasena(clave)) {
                throw new IllegalArgumentException("Por favor, introduce una contraseña válida que cumpla con los siguientes requisitos:\n" +
                "  • Al menos 6 caracteres de longitud.\n" +
                "  • Al menos una letra mayúscula.\n" +
                "  • Al menos una letra minúscula.\n" +
                "  • Al menos un dígito numérico.\n" +
                "  • Al menos un carácter especial [@#$%^&+=!].");
            }
        }
        this.clave = clave;
    }

    public boolean isBaja() {
        return baja;
    }

    public void setBaja() {
        this.baja = true;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void agregarPrestamo(Prestamo prestamo) {
        System.out.println(prestamos + "              " + prestamo);
        System.out.println(prestamos.contains(prestamo));
        if (!prestamos.contains(prestamo)) {
            prestamos.add(prestamo);
        }
    }

    public String toString() {
        return ControlUI.limitar(String.format("%s %s", nombre, apellido), 15);
    }
}
