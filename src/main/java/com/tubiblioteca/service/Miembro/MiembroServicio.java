package com.tubiblioteca.service.Miembro;

import java.util.ArrayList;
import java.util.List;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Prestamo;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class MiembroServicio extends CrudServicio<Miembro> {

    public MiembroServicio(Repositorio repositorio) {
        super(repositorio, Miembro.class);
    }

    @Override
    public Miembro validarEInsertar(Object... datos) {
        if (datos.length != 5) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String dni = (String) datos[0];
        String nombre = (String) datos[1];
        String apellido = (String) datos[2];
        TipoMiembro tipo = (TipoMiembro) datos[3];
        String clave = (String) datos[4];

        List<String> errores = new ArrayList<>();
        
        if (buscarPorId(dni) != null) {
            errores.add("El DNI ingresado ya está en uso. Por favor, ingrese otro DNI.");
        }

        try {
            new Miembro(dni, nombre, apellido, tipo, clave);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        Miembro nuevoMiembro = new Miembro(dni, nombre, apellido, tipo, clave);
        insertar(nuevoMiembro);
        return nuevoMiembro;
    }

    @Override
    public void validarYModificar(Miembro miembro, Object... datos) {
        if (datos.length != 4) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String nombre = (String) datos[0];
        String apellido = (String) datos[1];
        TipoMiembro tipo = (TipoMiembro) datos[2];
        String clave = (String) datos[3];
        Miembro aux = new Miembro();

        List<String> errores = new ArrayList<>();

        try {
            aux.setNombre(nombre);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }
        try {
            aux.setApellido(apellido);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }
        try {
            aux.setTipo(tipo);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }
        try {
            aux.setClave(clave);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        miembro.setNombre(nombre);
        miembro.setApellido(apellido);
        miembro.setTipo(tipo);
        miembro.setClave(clave);
        modificar(miembro);
    }

    @Override
    protected boolean esInactivo(Miembro miembro) {
        return miembro.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Miembro miembro) {
        miembro.setBaja();
    }

    @Override
    public boolean existe(Miembro miembro) {
        return buscarPorId(miembro.getDni()) != null 
        && !miembro.isBaja();
    }

    public void agregarPrestamo(Miembro miembro, Prestamo prestamo) {
        try{
            miembro.getPrestamos().add(prestamo);
            modificar(miembro);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}