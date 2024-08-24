package com.tubiblioteca.service.Miembro;

import java.util.ArrayList;
import java.util.List;

import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Prestamo;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.security.Contraseña;
import com.tubiblioteca.security.SesionManager;
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

        Miembro nuevoMiembro = new Miembro();

        try {
            nuevoMiembro = new Miembro(dni, nombre, apellido, tipo, clave);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        nuevoMiembro.setClave(Contraseña.codificar(clave), true);
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

        if (!clave.isEmpty()) {
            try {
                aux.setClave(clave, false);
            } catch (IllegalArgumentException e) {
                errores.add(e.getMessage());
            }
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        miembro.setNombre(nombre);
        miembro.setApellido(apellido);
        miembro.setTipo(tipo);
        if (!clave.isEmpty()) {
            miembro.setClave(Contraseña.codificar(clave), true);
        }
        modificar(miembro);
    }

    @Override
    public void validarYBorrar(Miembro miembro) {

        for (Prestamo prestamo : miembro.getPrestamos()) {
            if (!prestamo.isBaja()) {
                throw new IllegalArgumentException("No se puede eliminar el miembro de la biblioteca porque tiene préstamos asociados.");
            }
        }

        borrar(miembro);
    }

    @Override
    protected boolean esInactivo(Miembro miembro) {
        return miembro.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Miembro miembro) {
        miembro.setBaja();
    }

    public void agregarPrestamo(Miembro miembro, Prestamo prestamo) {
        try {
            miembro.getPrestamos().add(prestamo);
            modificar(miembro);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public void cambiarClave(String actual, String nueva, String nuevaRepetida) {

        List<String> errores = new ArrayList<>();

        Miembro aux = new Miembro();
        Miembro miembro = SesionManager.getMiembro();

        // Verificamos que la contrasena actual no este vacia y que coincida con la
        // antigua
        if (actual.isEmpty()) {
            errores.add("Por favor, ingrese la contraseña actual.");
        } else if (miembro != null) {
            if (!Contraseña.validar(actual, miembro.getClave())) {
                errores.add("La contraseña actual ingresada es incorrecta. Vuelva a intentarlo.");
            }
        }

        // Verificamos que la nueva contrasena no este vacia, que no supere los 50
        // caracteres y que cumpla los requisitos
        try {
            aux.setClave(nueva, false);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        // Verificamos que haya ingresado nuevamente la nueva contrasena y que coincidan
        if (nuevaRepetida.isEmpty()) {
            errores.add("Por favor, ingrese nuevamente la contraseña nueva.");
        } else if (!nuevaRepetida.equals(nueva)) {
            errores.add("La contraseña nueva no coincide. Vuelva a intentarlo.");
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        miembro.setClave(Contraseña.codificar(nueva), true);
        modificar(miembro);
    }

    public boolean validarCredenciales(String dni, String clave) {

        List<String> errores = new ArrayList<>();

        Miembro aux = new Miembro();

        try{
            aux.setDni(dni);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (clave.isEmpty()) {
            errores.add("Por favor, ingrese una contraseña.");
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        Miembro miembro = buscarPorId(dni);

        if (miembro != null) {
            if (Contraseña.validar(clave, miembro.getClave())) {
                return true;
            } else {
                throw new IllegalArgumentException("La contraseña ingresada es incorrecta. Vuelva a intentarlo.");
            }
        } else {
            throw new IllegalArgumentException("No se encontró ningún miembro de la biblioteca con el DNI especificado.");
        }
    }
}