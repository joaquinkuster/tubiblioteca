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

    public static Miembro seleccionarMiembro(Miembro miembro) {
        Pair<SelectorMiembroControlador, Parent> selector = StageManager
                .cargarVistaConControlador(Vista.SelectorMiembro.getRutaFxml());
        SelectorMiembroControlador controlador = selector.getKey();
        Parent vista = selector.getValue();

        if (miembro != null) {
            controlador.setMiembro(miembro);
        }

        StageManager.abrirModal(vista, Vista.SelectorMiembro);

        return controlador.getMiembro();
    }

    public static Libro seleccionarLibro(Libro libro) {
        Pair<SelectorLibroControlador, Parent> selector = StageManager
                .cargarVistaConControlador(Vista.SelectorLibro.getRutaFxml());
        SelectorLibroControlador controlador = selector.getKey();
        Parent vista = selector.getValue();

        if (libro != null) {
            controlador.setLibro(libro);
        }

        StageManager.abrirModal(vista, Vista.SelectorLibro);

        return controlador.getLibro();
    }

    public static CopiaLibro seleccionarCopiaLibro(CopiaLibro copia) {
        Pair<SelectorCopiaLibroControlador, Parent> selector = StageManager
                .cargarVistaConControlador(Vista.SelectorCopiaLibro.getRutaFxml());
        SelectorCopiaLibroControlador controlador = selector.getKey();
        Parent vista = selector.getValue();

        if (copia != null) {
            controlador.setCopiaLibro(copia);
        }

        StageManager.abrirModal(vista, Vista.SelectorCopiaLibro);

        return controlador.getCopiaLibro();
    }
}