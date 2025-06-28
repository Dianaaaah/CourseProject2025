package com.example.mycompany.controller;

import com.example.mycompany.dao.UserDAO;
import com.example.mycompany.view.RegisterView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private RegisterView registerView;

    public void setRegisterView(RegisterView registerView) {
        this.registerView = registerView;
    }

    @FXML
    private void onRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("Заполните все поля!", false);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showMessage("Пароли не совпадают!", false);
            return;
        }
        
        boolean success = UserDAO.getInstance().register(username, password);
        if (success) {
            showMessage("Регистрация успешна! Теперь войдите.", true);
        } else {
            showMessage("Ошибка регистрации", false);
        }
    }

    @FXML
    private void onBackToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            new com.example.mycompany.view.LoginView(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setVisible(true);
    }
} 