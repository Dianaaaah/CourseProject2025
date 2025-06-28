package com.example.mycompany.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainView {
    public MainView(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mycompany/main-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Главное меню");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 