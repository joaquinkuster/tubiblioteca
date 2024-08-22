package com.tubiblioteca.service.CopiaLibro;

import java.util.ArrayList;
import java.util.List;
import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.EstadoCopiaLibro;
import com.tubiblioteca.model.Libro;
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
            if (!servicioLibro.existe(libro)) {
                errores.add("El libro seleccionado no se encuentra en la base de datos.");
            }
        }

        if (rack != null) {
            if (!servicioRack.existe(rack)) {
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
        return nuevaCopia;
    }

    @Override
    public void validarYModificar(CopiaLibro copia, Object... datos) {
        if (datos.length != 5) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        TipoCopiaLibro tipo = (TipoCopiaLibro) datos[0];
        String precio = (String) datos[1];
        Libro libro = (Libro) datos[2];
        Rack rack = (Rack) datos[3];
        boolean referencia = (boolean) datos[4];
        CopiaLibro aux = new CopiaLibro();

        List<String> errores = new ArrayList<>();

        // Validamos si el libro o el rack seleccionados existen
        if (libro != null) {
            if (!servicioLibro.existe(libro)) {
                errores.add("El libro seleccionado no se encuentra en la base de datos.");
            }
        }

        if (rack != null) {
            if (!servicioRack.existe(rack)) {
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

        copia.setTipo(tipo);
        copia.setPrecio(precio);
        copia.setLibro(libro);
        copia.setRack(rack);
        copia.setReferencia(referencia);
        modificar(copia);
    }

    @Override
    protected boolean esInactivo(CopiaLibro copia) {
        return copia.getEstado() == EstadoCopiaLibro.Perdida;
    }

    @Override
    protected void marcarComoInactivo(CopiaLibro copia) {
        copia.setEstado(EstadoCopiaLibro.Perdida);
    }

    @Override
    public boolean existe(CopiaLibro copia) {
        return buscarPorId(copia.getId()) != null
                && !esInactivo(copia);
    }

    public void modificarEstado(CopiaLibro copia, EstadoCopiaLibro estado) {
        try {
            copia.setEstado(estado);
            modificar(copia);
        } catch (Exception e) {
            throw e;
        }
    }
}