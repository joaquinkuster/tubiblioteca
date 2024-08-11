package com.tubiblioteca.helper;

import java.util.regex.Pattern;

public class Validacion {

    // Regex utilizados para validar diferentes campos
    private static final String regexDni = "^(\\d{8}|[MF]\\d{7})$";
    private static final String regexContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])\\S{6,}$";
    private static final String regexNombreApellido = "^[A-Za-zÁáÉéÍíÓóÚúÜü]+(?:\\s+[A-Za-zÁáÉéÍíÓóÚúÜü]+)*$";

    // Metodo para validar un DNI
    public static boolean validarDni(String dni) {
        return !Pattern.matches(regexDni, dni);
    }

    // Metodo para validar una contrasena
    public static boolean validarContrasena(String contrasena) {
        return !Pattern.matches(regexContrasena, contrasena);
    }

    // Metodo para validar un nombre o apellido
    public static boolean validarNombreApellido(String nombre) {
        return !Pattern.matches(regexNombreApellido, nombre);
    }

}
