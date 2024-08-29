package com.tubiblioteca.config;

import com.tubiblioteca.repository.Repositorio;

public class AppConfig {

    // Variable estática que mantiene una instancia del repositorio
    private static Repositorio repositorio;
   
    // Método estático para obtener la instancia actual del repositorio
    public static Repositorio getRepositorio() {
        return repositorio;
    }

    // Método estático para establecer la instancia del repositorio
    public static void setRepositorio(Repositorio rep) {
        repositorio = rep;
    }

}
