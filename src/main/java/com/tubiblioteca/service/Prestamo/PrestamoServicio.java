package com.tubiblioteca.service.Prestamo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.EstadoCopiaLibro;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.Prestamo;
import com.tubiblioteca.repository.Repositorio;
import com.tubiblioteca.service.CrudServicio;
import com.tubiblioteca.service.CopiaLibro.CopiaLibroServicio;
import com.tubiblioteca.service.Miembro.MiembroServicio;

public class PrestamoServicio extends CrudServicio<Prestamo> {

    // Servicios utilizados para las validaciones
    private MiembroServicio servicioMiembro;
    private CopiaLibroServicio servicioCopia;

    public PrestamoServicio(Repositorio repositorio) {
        super(repositorio, Prestamo.class);
        servicioMiembro = new MiembroServicio(repositorio);
        servicioCopia = new CopiaLibroServicio(repositorio);
    }

    @Override
    public Prestamo validarEInsertar(Object... datos) {
        if (datos.length != 3) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        LocalDate fechaPrestamo = (LocalDate) datos[0];
        Miembro miembro = (Miembro) datos[1];
        CopiaLibro copia = (CopiaLibro) datos[2];

        List<String> errores = new ArrayList<>();

        // Validamos si el miembro o la copia seleccionados existen
        if (miembro != null) {
            if (!servicioMiembro.existe(miembro, miembro.getDni())) {
                errores.add("El miembro de la biblioteca seleccionado no se encuentra en la base de datos.");
            }
        }

        if (copia != null) {
            if (!servicioCopia.existe(copia, copia.getId())) {
                errores.add("La copia del libro seleccionada no se encuentra en la base de datos.");
            }
        }

        Prestamo nuevoPrestamo = new Prestamo();

        try {
            nuevoPrestamo = new Prestamo(fechaPrestamo, miembro, copia);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        try {
            if (miembro != null && copia != null) {
                validarPrestamo(nuevoPrestamo);
            }
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        insertar(nuevoPrestamo);
        servicioCopia.modificarEstado(copia, EstadoCopiaLibro.Prestada);
        agregarPrestamoAEntidades(nuevoPrestamo, miembro, copia);
        return nuevoPrestamo;
    }

    @Override
    public void validarYModificar(Prestamo prestamo, Object... datos) {
        if (datos.length != 3) {
            throw new IllegalArgumentException("Número incorrecto de parámetros.");
        }

        LocalDate fechaDevolucion = (LocalDate) datos[0];
        CopiaLibro copia = (CopiaLibro) datos[1];
        String multa = (String) datos[2];
        Prestamo aux = new Prestamo();

        List<String> errores = new ArrayList<>();

        boolean hayDevolucion = fechaDevolucion != null || prestamo.getFechaDevolucion() != null; 

        if (hayDevolucion) {
            try {
                aux.setFechaDevolucion(fechaDevolucion);
            } catch (IllegalArgumentException e) {
                errores.add(e.getMessage());
            }
        }

        try {
            aux.setMulta(multa);
        } catch (IllegalArgumentException e) {
            errores.add(e.getMessage());
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }

        if (hayDevolucion) {
            prestamo.setFechaDevolucion(fechaDevolucion);
        }
        prestamo.setMulta(multa);
        modificar(prestamo);

        if (!copia.getEstado().equals(EstadoCopiaLibro.Disponible)) {
            servicioCopia.modificarEstado(copia, EstadoCopiaLibro.Disponible);
        }
    }

    @Override
    public void validarYBorrar(Prestamo prestamo) {

        if (prestamo.getFechaDevolucion() == null) {
            servicioCopia.modificarEstado(prestamo.getCopiaLibro(), EstadoCopiaLibro.Disponible);
        }

        borrar(prestamo);
    }

    @Override
    protected boolean esInactivo(Prestamo prestamo) {
        return prestamo.isBaja();
    }

    @Override
    protected void marcarComoInactivo(Prestamo prestamo) {
        prestamo.setBaja();
    }

    public void cerrarPrestamo(Prestamo prestamo) {
        if (prestamo.getFechaDevolucion() != null) {
            throw new IllegalStateException("La devolución de este préstamo ya ha sido registrada anteriormente.");
        }
        try {
            prestamo.setFechaDevolucion(LocalDate.now());
            modificar(prestamo);
            servicioCopia.modificarEstado(prestamo.getCopiaLibro(), EstadoCopiaLibro.Disponible);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public void validarPrestamo(Prestamo prestamo) {

        Miembro miembro = prestamo.getMiembro();
        CopiaLibro copia = prestamo.getCopiaLibro();
        List<Prestamo> prestamos = miembro.getPrestamos();

        List<String> errores = new ArrayList<>();

        int activos = 0;
        boolean prestamoVencido = false;

        if (prestamos != null) {
            for (Prestamo p : prestamos) {
                if (existe(p, p.getId()) && p.getFechaDevolucion() == null) {
                    activos++;
                    if (p.getFechaPrestamo().plusDays(10).isBefore(LocalDate.now())) {
                        prestamoVencido = true;
                    }
                }
            }
        }

        if (activos >= 5) {
            errores.add("El miembro ya tiene 5 préstamos activos y no puede realizar otro hasta devolver alguno.");
        }

        if (prestamoVencido) {
            errores.add(
                    "El miembro tiene al menos un préstamo vencido y no puede realizar nuevos préstamos hasta regularizar su situación.");
        }

        if (copia.isReferencia()) {
            errores.add("No se puede prestar una copia de libro de referencia.");
        }

        if (copia.getEstado() == EstadoCopiaLibro.Prestada) {
            errores.add(
                    "Esta copia de libro ya está prestada y no puede ser prestada nuevamente hasta que la devuelvan.");
        } else if (copia.getEstado() == EstadoCopiaLibro.Perdida) {
            errores.add("Esta copia de libro está perdida y no puede ser prestada.");
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errores));
        }
    }

    private void agregarPrestamoAEntidades(Prestamo prestamo, Miembro miembro, CopiaLibro copia){
        servicioMiembro.agregarPrestamo(miembro, prestamo);
        servicioCopia.agregarPrestamo(copia, prestamo);
    }
}