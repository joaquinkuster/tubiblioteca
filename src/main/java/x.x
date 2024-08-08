module edu.unam {
    requires javafx.controls;
    requires javafx.fxml;
    requires eclipselink;
    requires jakarta.persistence;
    requires javafx.graphics;

    opens edu.unam.controladores to javafx.fxml;
    opens edu.unam.modelo to eclipselink, javafx.base;
    exports edu.unam;
}
