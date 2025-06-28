module com.example.mycompany {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.mycompany to javafx.fxml;
    opens com.example.mycompany.model to javafx.fxml;
    opens com.example.mycompany.controller to javafx.fxml;

    exports com.example.mycompany;
    exports com.example.mycompany.model;
    exports com.example.mycompany.controller;
}