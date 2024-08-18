module com.tubiblioteca {
    // Declarar las dependencias del módulo
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires eclipselink;
    requires jakarta.persistence;
    requires org.slf4j;

    // Abrir el paquete com.tubiblioteca a javafx.fxml para permitir la carga de FXML
    opens com.tubiblioteca.controller to javafx.fxml;
    opens com.tubiblioteca.controller.ABMMiembro to javafx.fxml;
    opens com.tubiblioteca.controller.ABMEditorial to javafx.fxml;
    opens com.tubiblioteca.controller.ABMAutor to javafx.fxml;
    opens com.tubiblioteca.controller.ABMCategoria to javafx.fxml;
    opens com.tubiblioteca.controller.ABMLibro to javafx.fxml;
    opens com.tubiblioteca.controller.ABMIdioma to javafx.fxml;
    opens com.tubiblioteca.model to eclipselink, javafx.base;

    // Exportar el paquete com.tubiblioteca para que otros módulos puedan acceder a sus clases públicas
    exports com.tubiblioteca;
}
