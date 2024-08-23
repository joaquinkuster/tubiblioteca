package com.tubiblioteca.security;

import com.tubiblioteca.config.StageManager;
import com.tubiblioteca.helper.Alerta;
import com.tubiblioteca.model.Miembro;
import com.tubiblioteca.model.TipoMiembro;
import com.tubiblioteca.view.Vista;

public class SesionManager {

    // Información del usuario actualmente autenticado
    private static Miembro miembro;

    /**
     * Verifica si hay una sesión activa.
     * 
     */
    public static boolean haySesion(){
        return miembro != null;
    }

    /**
     * Abre una sesión para el miembro especificado.
     * 
     * @param miembro El miembro que está iniciando sesión.
     */
    public static void abrirSesion(Miembro miembro) {
        // Establece el miembro actual
        SesionManager.miembro = miembro;

        // Muestra un mensaje de éxito
        mostrarMensaje("Se ha iniciado sesión correctamente!");

        // Cambia a la vista principal
        StageManager.cambiarEscena(Vista.VistaPrincipal);
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    public static void cerrarSesion() {
        // Limpia la información del usuario
        miembro = null;

        // Muestra un mensaje de éxito
        mostrarMensaje("Se ha cerrado sesión correctamente!");

        // Cambia a la vista de login
        StageManager.cambiarEscena(Vista.Login);
    }

    /**
     * Obtiene el miembro actualmente autenticado.
     * 
     * @return El miembro actualmente autenticado.
     */
    public static Miembro getMiembro() {
        return miembro;
    }

    /**
     * Verifica si el miembro actual es un usuario.
     * 
     * @return true si el miembro es un usuario, false en caso contrario.
     */
    public static boolean esUsuario() {
        return haySesion() && miembro.getTipo() == TipoMiembro.Usuario;
    }

    /**
     * Muestra un mensaje de información.
     * 
     * @param contenido El contenido del mensaje.
     */
    private static void mostrarMensaje(String contenido) {
        Alerta.mostrarMensaje(false, "Info", contenido);
    }
}
