package com.tubiblioteca;

// Esta clase se crea para no tener que usar el sistema de módulos de java
// lo que se define en module-info.java
// van a ver que le genera una advertencia pero debe funcionar sin problemas

// Si quieren usar el sistema de módulos, pueden hacerlo.
// quiten esta clase y modifiquen el nombre del archivo x.x. por module-info.java
// en este caso el punto de inicio se encuentra en App.java
public class AppLauncher {
    public static void main(String[] args) {
        App.main(args);
    }
}