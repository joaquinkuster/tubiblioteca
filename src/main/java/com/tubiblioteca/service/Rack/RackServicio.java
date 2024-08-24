package com.tubiblioteca.service.Rack;

import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.model.Rack;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;

public class RackServicio extends CrudServicio<Rack> {

    public RackServicio(Repositorio repositorio) {
        super(repositorio, Rack.class);
    }

    @Override
    public Rack validarEInsertar(Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String descripcion = (String) datos[0];

        try {
            Rack nuevoRack = new Rack(descripcion);
            insertar(nuevoRack);
            return nuevoRack;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void validarYModificar(Rack rack, Object... datos) {
        if (datos.length != 1) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        String descripcion = (String) datos[0];
        Rack aux = new Rack();

        try {
            aux.setDescripcion(descripcion);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        rack.setDescripcion(descripcion);
        modificar(rack);
    }

    @Override
    protected boolean esInactivo(Rack rack) {
        return rack.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Rack rack) {
        rack.setBaja();
    }

    @Override
    public void validarYBorrar(Rack rack) {

        for (CopiaLibro copia : rack.getCopiasLibros()) {
            if (!copia.isBaja()) {
                throw new IllegalArgumentException("No se puede eliminar el rack porque tiene copias de libros asociadas.");
            }
        }

        borrar(rack);
    }
}