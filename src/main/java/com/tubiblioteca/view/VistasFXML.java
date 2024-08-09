package com.tubiblioteca.view;

// Declaramos un enumerado con todas las vistas de la aplicacion
public enum VistasFXML {

    // Cada vista tiene asociada una clave de título y una ruta de archivo FXML
    Login("Iniciar Sesión", "Login"),
    VistaPrincipal("", "VistaPrincipal"),

    ListaMiembros("Lista de Miembros", "ABMMiembro/ListaMiembros"),
    FormularioMiembro("Formulario de Miembro", "ABMMiembro/FormularioMiembro"),
    SelectorMiembro("Selector de Miembro", "ABMMiembro/SelectorMiembro");

    // Devolvemos la clave
    private final String titulo;

    private final String rutaFxml;

    VistasFXML(String titulo, String rutaFxml) {
        this.titulo = titulo;
        this.rutaFxml = rutaFxml;
    }

    // Devolvemos el titulo completo de la vista
    public String getTitulo() {
        return "TuBiblioteca | " + ((!titulo.isEmpty()) ? titulo : "Sin Título");
    }

    // Devolvemos la ruta completa del archivo FXML
    public String getRutaFxml() {
        return String.format("/ui/fxml/%s.fxml", rutaFxml);
    }
}