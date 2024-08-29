package com.tubiblioteca.view;

// Declaramos un enumerado con todas las vistas de la aplicación
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
    SelectorLibro("Selector de Libro", "ABMLibro/SelectorLibro"),
    
    ListaIdiomas("LIsta de Idiomas", "ABMIdioma/ListaIdiomas"),
    FormularioIdioma("Formulario de Idioma", "ABMIdioma/FormularioIdioma"),

    ListaPrestamos("LIsta de Préstamos", "ABMPrestamo/ListaPrestamos"),
    FormularioPrestamo("Formulario de Préstamo", "ABMPrestamo/FormularioPrestamo"),

    ListaCopiasLibros("Lista de Copias de Libros", "ABMCopiaLibro/ListaCopiasLibros"),
    FormularioCopiaLibro("Formulario de Copia del Libro", "ABMCopiaLibro/FormularioCopiaLibro"),
    SelectorCopiaLibro("Selector de Copia del Libro", "ABMCopiaLibro/SelectorCopiaLibro"),

    ListaRacks("Lista de Racks", "ABMRack/ListaRacks"),
    FormularioRack("Formulario de Rack", "ABMRack/FormularioRack"),

    ListaAuditoria("Auditoría", "Auditoria/ListaAuditoria"),

    CambiarContraseña("Cambiar Contraseña", "CambiarContraseña");

    // Devolvemos la clave
    private final String titulo;  // Título de la vista, se usa para mostrar en la interfaz de usuario
    private final String rutaFxml;  // Ruta relativa del archivo FXML que define la vista

    // Constructor del enumerado, se utiliza para inicializar las constantes con un título y una ruta FXML
    private Vista(String titulo, String rutaFxml) {
        this.titulo = titulo;
        this.rutaFxml = rutaFxml;
    }

    // Devuelve el título completo de la vista, incluyendo el prefijo "TuBiblioteca | "
    public String getTitulo() {
        return "TuBiblioteca | " + ((!titulo.isEmpty()) ? titulo : "Sin Título");
    }

    // Devuelve la ruta completa del archivo FXML correspondiente a la vista
    public String getRutaFxml() {
        return String.format(("fxml/%s.fxml"), rutaFxml);  // Se asume que todos los archivos FXML están en la carpeta "fxml"
    }
}
