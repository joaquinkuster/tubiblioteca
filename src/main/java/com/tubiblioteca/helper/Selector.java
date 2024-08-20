package com.tubiblioteca.helper;

import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.controller.ABMMiembro.SelectorMiembroControlador;
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

}