package com.tubiblioteca.service.CopiaLibro;

import java.util.ArrayList;
import java.util.List;

import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.EstadoCopiaLibro;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.model.Prestamo;
import com.tubiblioteca.model.Rack;
import com.tubiblioteca.model.TipoCopiaLibro;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;
import com.tubiblioteca.service.Libro.LibroServicio;
import com.tubiblioteca.service.Rack.RackServicio;

public class CopiaLibroServicio extends CrudServicio<CopiaLibro> {

    // Servicios utilizados para las validaciones
    private LibroServicio servicioLibro;
    private RackServicio servicioRack;

    public CopiaLibroServicio(Repositorio repositorio) {
        super(repositorio, CopiaLibro.class);
        servicioLibro = new LibroServicio(repositorio);
        servicioRack = new RackServicio(repositorio);
    }

    @Override
    public CopiaLibro validarEInsertar(Object... datos) {
        if (datos.length != 5) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        TipoCopiaLibro tipo = (TipoCopiaLibro) datos[0];
        String precio = (String) datos[1];
        Libro libro = (Libro) datos[2];
        Rack rack = (Rack) datos[3];
        boolean referencia = (boolean) datos[4];

        List<String> errores = new ArrayList<>();

        // Validamos si el libro o el rack seleccionados existen
        if (libro != null) {
            if (!servicioLibro.existe(libro, libro.getIsbn())) {
                errores.add("El libro seleccionado no se encuentra en la base de datos.");
            }
        }

        if (rack != null) {
            if (!servicioRack.existe(rack, rack.getId())) {
                errores.add("El rack seleccionada no se encuentra en la base de datos.");
            }
        }

        CopiaLibro nuevaCopia = new CopiaLibro();

        try {
            nuevaCopia = new CopiaLibro(tipo, precio, libro, rack, referencia);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        insertar(nuevaCopia);
        agregarCopiaAEntidades(nuevaCopia, libro, rack);
        return nuevaCopia;
    }

    @Override
    public void validarYModificar(CopiaLibro copia, Object... datos) {
        if (datos.length != 6) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        TipoCopiaLibro tipo = (TipoCopiaLibro) datos[0];
        String precio = (String) datos[1];
        Libro libro = (Libro) datos[2];
        Rack rack = (Rack) datos[3];
        boolean referencia = (boolean) datos[4];
        boolean estaPerdida = (boolean) datos[5];
        CopiaLibro aux = new CopiaLibro();

        List<String> errores = new ArrayList<>();

        // Validamos si el libro o el rack seleccionados existen
        if (libro != null) {
            if (!servicioLibro.existe(libro, libro.getIsbn())) {
                errores.add("El libro seleccionado no se encuentra en la base de datos.");
            }
        }

        if (rack != null) {
            if (!servicioRack.existe(rack, rack.getId())) {
                errores.add("El rack seleccionada no se encuentra en la base de datos.");
            }
        }

        try {
            aux.setTipo(tipo);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        try {
            aux.setPrecio(precio);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        try {
            aux.setLibro(libro);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        try {
            aux.setRack(rack);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        if (referencia && copia.getEstado().equals(EstadoCopiaLibro.Prestada)) {
            throw new IllegalArgumentException("No se puede marcar como 'referencia' porque hay un préstamo en curso.");
        }

        if (estaPerdida) {
            try {
                modificarEstado(copia, EstadoCopiaLibro.Perdida);
            } catch (IllegalArgumentException e) {
                throw e;
            }
        } else {
            if (copia.getEstado().equals(EstadoCopiaLibro.Perdida)) {
                modificarEstado(copia, EstadoCopiaLibro.Disponible);
            }
        }

        agregarCopiaAEntidades(copia, libro, rack);
        copia.setTipo(tipo);
        copia.setPrecio(precio);
        copia.setLibro(libro);
        copia.setRack(rack);
        copia.setReferencia(referencia);
        modificar(copia);
    }

    @Override
    public void validarYBorrar(CopiaLibro copia) {

        for (Prestamo prestamo : copia.getPrestamos()) {
            if (!prestamo.isBaja()) {
                String mensaje = "No se puede eliminar la copia porque tiene préstamos asociados.\n" +
                        "Puede marcarla como 'perdida' en su lugar.";
                throw new IllegalArgumentException(mensaje);
            }
        }

        borrar(copia);
    }

    @Override
    protected boolean esInactivo(CopiaLibro copia) {
        return copia.isBaja();
    }

    @Override
    protected void marcarComoInactivo(CopiaLibro copia) {
        copia.setBaja();
    }

    public String verificarReferencias(List<Libro> libros) {
        // Si no hay libros entonces salimos de la funcion inmediatamente
        if (libros.isEmpty() || libros == null) {
            throw new IllegalArgumentException("No existe ningún libro registrado.");
        }

        List<String> respuesta = new ArrayList<>();

        for (Libro libro : libros) {
            // Verificar las referencias
            if (!libro.getCopias().isEmpty()) {
                boolean tieneReferencia = false;

                // Verificar si alguna copia del libro es de referencia
                for (CopiaLibro copia : libro.getCopias()) {
                    if (copia.isReferencia()) {
                        tieneReferencia = true;
                        break;
                    }
                }

                // Si el libro no tiene copias de referencia, lo agregamos a la lista
                if (!tieneReferencia) {
                    respuesta.add("El libro " + libro + " no tiene referencia.");
                }
            } else {
                respuesta.add("El libro " + libro + " no tiene copias registradas.");
            }
        }

        if (respuesta.isEmpty()) {
            return "Todos los libros tienen referencias.";
        } else {
            return String.join("\n", respuesta);
        }
    }

    public void modificarEstado(CopiaLibro copia, EstadoCopiaLibro estado) {
        try {

            if (copia.getEstado().equals(EstadoCopiaLibro.Prestada)) {
                if (estado.equals(EstadoCopiaLibro.Perdida)) {
                    throw new IllegalArgumentException(
                            "No se puede marcar como 'perdida' porque hay un préstamo en curso.");
                }
            }

            copia.setEstado(estado);
            modificar(copia);

        } catch (Exception e) {
            throw e;
        }
    }

    public void agregarPrestamo(CopiaLibro copia, Prestamo prestamo) {
        try {
            copia.agregarPrestamo(prestamo);
            modificar(copia);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    private void agregarCopiaAEntidades(CopiaLibro copia, Libro libro, Rack rack) {
        servicioLibro.agregarQuitarCopia(libro, copia);
        servicioRack.agregarQuitarCopia(rack, copia);
    }
}