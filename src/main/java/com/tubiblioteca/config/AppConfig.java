package com.tubiblioteca.config;

import com.tubiblioteca.repository.Repositorio;

public class AppConfig {

    private static Repositorio repositorio;
   
    // Uso de repositorio
    public static Repositorio getRepositorio() {
        return repositorio;
    }

    public static void setRepositorio(Repositorio rep) {
        repositorio = rep;
    }

}
