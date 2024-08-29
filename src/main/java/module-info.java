module com.tubiblioteca {
    // Declarar las dependencias del módulo
    requires javafx.controls;         // Requiere el módulo de controles de JavaFX para UI
    requires javafx.fxml;            // Requiere el módulo de FXML de JavaFX para cargar archivos FXML
    requires java.sql;               // Requiere el módulo de SQL para conexiones y operaciones de base de datos
    requires javafx.graphics;        // Requiere el módulo de gráficos de JavaFX para renderizar gráficos y UI
    requires eclipselink;            // Requiere EclipseLink para la persistencia de datos (JPA)
    requires jakarta.persistence;    // Requiere Jakarta Persistence API para trabajar con JPA
    requires org.slf4j;              // Requiere SLF4J para el logging
    requires org.controlsfx.controls; // Requiere ControlsFX para controles adicionales en JavaFX
    requires java.prefs;             // Requiere el módulo de preferencias de Java para almacenar configuraciones
    requires spring.security.crypto; // Requiere Spring Security Crypto para encriptación y seguridad

    // Abrir el paquete com.tubiblioteca a javafx.fxml para permitir la carga de FXML
    opens com.tubiblioteca.controller to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller
    opens com.tubiblioteca.controller.ABMMiembro to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMMiembro
    opens com.tubiblioteca.controller.ABMEditorial to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMEditorial
    opens com.tubiblioteca.controller.ABMAutor to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMAutor
    opens com.tubiblioteca.controller.ABMCategoria to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMCategoria
    opens com.tubiblioteca.controller.ABMLibro to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMLibro
    opens com.tubiblioteca.controller.ABMIdioma to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMIdioma
    opens com.tubiblioteca.controller.ABMPrestamo to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMPrestamo
    opens com.tubiblioteca.controller.ABMRack to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMRack
    opens com.tubiblioteca.controller.ABMCopiaLibro to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.ABMCopiaLibro
    opens com.tubiblioteca.controller.Auditoria to javafx.fxml; // Permite a javafx.fxml acceder a las clases en com.tubiblioteca.controller.Auditoria
    opens com.tubiblioteca.model to eclipselink, javafx.base; // Permite a eclipselink y javafx.base acceder a las clases en com.tubiblioteca.model
    
    // Exportar el paquete com.tubiblioteca para que otros módulos puedan acceder a sus clases públicas
    exports com.tubiblioteca; // Exporta el paquete com.tubiblioteca para que otros módulos puedan usar sus clases

    exports com.tubiblioteca.auditoria; // Exporta el paquete com.tubiblioteca.auditoria para uso externo

    exports com.tubiblioteca.controller.Auditoria to javafx.fxml; // Exporta el paquete com.tubiblioteca.controller.Auditoria solo a javafx.fxml
}
