module com.example.mycompany {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mycompany to javafx.fxml;
    exports com.example.mycompany;
}