package com.tubiblioteca.view;

// Declaramos un enumerado con todas las vistas de la aplicacion
public enum Vista {

    // Cada vista tiene asociada una clave de título y una ruta de archivo FXML
    Login("Iniciar Sesión", "Login"),
    VistaPrincipal("", "Principal"),
    Menu("", "Menu"),

    ListaMiembros("Lista de Miembros", "ABMMiembro/ListaMiembros"),
    FormularioMiembro("Formulario de Miembro", "ABMMiembro/FormularioMiembro"),
    SelectorMiembro("Selector de Miembro", "ABMMiembro/SelectorMiembro"),

    ListaEditoriales("Lista de Editoriales", "ABMEditorial/ListaEditoriales"),
    FormularioEditorial("Formulario de Editorial", "ABMEditorial/FormularioEditorial"),

    ListaAutores("Lista de Autores", "ABMAutor/ListaAutores"),
    FormularioAutor("Formulario de Autores", "ABMAutor/FormularioAutor"),
    
    ListaCategorias("Lista de Categorías", "ABMCategoria/ListaCategorias"),
    FormularioCategoria("Formulario de Categoría", "ABMCategoria/FormularioCategoria"),

    ListaLibros("Lista de Libros", "ABMLibro/ListaLibros"),
    FormularioLibro("Formulario de Libro", "ABMLibro/FormularioLibro"),
    
    ListaIdiomas("LIsta de Idiomas", "ABMIdioma/ListaIdiomas"),
    FormularioIdioma("Formulario de Idioma", "ABMIdioma/FormularioIdioma"),

    ListaPrestamos("LIsta de Préstamos", "ABMPrestamo/ListaPrestamos"),
    FormularioPrestamo("Formulario de Préstamo", "ABMPrestamo/FormularioPrestamo");

    // Devolvemos la clave
    private final String titulo;

    private final String rutaFxml;

    Vista(String titulo, String rutaFxml) {
        this.titulo = titulo;
        this.rutaFxml = rutaFxml;
    }

    // Devolvemos el titulo completo de la vista
    public String getTitulo() {
        return "TuBiblioteca | " + ((!titulo.isEmpty()) ? titulo : "Sin Título");
    }

    // Devolvemos la ruta completa del archivo FXML
    public String getRutaFxml() {
        return String.format(("fxml/%s.fxml"), rutaFxml);
    }
}