package com.tubiblioteca.helper;

import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.controller.ABMCopiaLibro.SelectorCopiaLibroControlador;
import com.tubiblioteca.controller.ABMLibro.SelectorLibroControlador;
import com.tubiblioteca.controller.ABMMiembro.SelectorMiembroControlador;
import com.tubiblioteca.model.CopiaLibro;
import com.tubiblioteca.model.Libro;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.view.Vista;

import javafx.scene.Parent;
import javafx.util.Pair;

public class Selector {

    // Método para seleccionar un miembro
    public static Miembro seleccionarMiembro(Miembro miembro) {
        // Carga la vista y el controlador para seleccionar un miembro
        Pair<SelectorMiembroControlador, Parent> selector = StageManager
                .cargarVistaConControlador(Vista.SelectorMiembro.getRutaFxml());
        SelectorMiembroControlador controlador = selector.getKey();
        Parent vista = selector.getValue();

        // Si se proporciona un miembro, lo establece en el controlador
        if (miembro != null) {
            controlador.setMiembro(miembro);
        }

        // Abre la vista como un modal
        StageManager.abrirModal(vista, Vista.SelectorMiembro);

        // Retorna el miembro seleccionado desde el controlador
        return controlador.getMiembro();
    }

    // Método para seleccionar un libro
    public static Libro seleccionarLibro(Libro libro) {
        // Carga la vista y el controlador para seleccionar un libro
        Pair<SelectorLibroControlador, Parent> selector = StageManager
                .cargarVistaConControlador(Vista.SelectorLibro.getRutaFxml());
        SelectorLibroControlador controlador = selector.getKey();
        Parent vista = selector.getValue();

        // Si se proporciona un libro, lo establece en el controlador
        if (libro != null) {
            controlador.setLibro(libro);
        }

        // Abre la vista como un modal
        StageManager.abrirModal(vista, Vista.SelectorLibro);

        // Retorna el libro seleccionado desde el controlador
        return controlador.getLibro();
    }

    // Método para seleccionar una copia de un libro
    public static CopiaLibro seleccionarCopiaLibro(CopiaLibro copia) {
        // Carga la vista y el controlador para seleccionar una copia de libro
        Pair<SelectorCopiaLibroControlador, Parent> selector = StageManager
                .cargarVistaConControlador(Vista.SelectorCopiaLibro.getRutaFxml());
        SelectorCopiaLibroControlador controlador = selector.getKey();
        Parent vista = selector.getValue();

        // Si se proporciona una copia de libro, la establece en el controlador
        if (copia != null) {
            controlador.setCopiaLibro(copia);
        }

        // Abre la vista como un modal
        StageManager.abrirModal(vista, Vista.SelectorCopiaLibro);

        // Retorna la copia de libro seleccionada desde el controlador
        return controlador.getCopiaLibro();
    }
}
