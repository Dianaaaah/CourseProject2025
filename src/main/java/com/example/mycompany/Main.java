package com.example.mycompany;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.mycompany.view.LoginView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        new LoginView(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
} 