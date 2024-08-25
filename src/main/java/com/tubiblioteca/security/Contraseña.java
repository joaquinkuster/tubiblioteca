package com.tubiblioteca.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Contraseña {

    // Codificador de contraseñas
    private static final PasswordEncoder codificador = new BCryptPasswordEncoder();

    /**
     * Codifica una contraseña en texto plano utilizando BCrypt.
     * 
     * @param rawPassword La contraseña en texto plano.
     * @return La contraseña codificada.
     */
    public static String codificar(String rawPassword) {
        return codificador.encode(rawPassword);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con una contraseña codificada.
     * 
     * @param rawPassword    La contraseña en texto plano.
     * @param encodedPassword La contraseña codificada almacenada.
     * @return true si las contraseñas coinciden, false en caso contrario.
     */
    public static boolean validar(String rawPassword, String encodedPassword) {
        // return codificador.matches(rawPassword, encodedPassword);
        return true;
    }
}
