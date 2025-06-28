package com.example.mycompany.view;

import com.example.mycompany.controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class LoginView {
    private Label messageLabel;

    public LoginView(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mycompany/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Вход в систему");
        stage.setScene(scene);
        stage.show();
            messageLabel = (Label) root.lookup("#messageLabel");
            LoginController controller = loader.getController();
            controller.setLoginView(this);
            controller.setStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setVisible(true);
        }
    }

    public void showSuccess(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setVisible(true);
        }
    }
} 