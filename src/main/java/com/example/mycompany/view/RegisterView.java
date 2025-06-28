package com.example.mycompany.view;

import com.example.mycompany.controller.RegisterController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class RegisterView {
    private Label messageLabel;

    public RegisterView(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mycompany/register-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Регистрация");
            stage.setScene(scene);
            stage.show();
            // Получаем ссылку на messageLabel через fx:id
            messageLabel = (Label) root.lookup("#messageLabel");
            // Передаём ссылку на RegisterView в контроллер
            RegisterController controller = loader.getController();
            controller.setRegisterView(this);
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