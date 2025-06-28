package com.example.mycompany.controller;

import com.example.mycompany.dao.UserDAO;
import com.example.mycompany.model.User;
import com.example.mycompany.model.Session;
import com.example.mycompany.view.LoginView;
import com.example.mycompany.view.RegisterView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private LoginView loginView;
    private Stage stage;

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
        User user = UserDAO.getInstance().authenticate(username, password);
        if (user != null) {
                Session.setUser(user.getRoleName(), user.getId(), user.getUsername());
                loginView.showSuccess("Успешный вход! Добро пожаловать, " + user.getUsername() + 
                    " (" + (Session.isAdmin() ? "Администратор" : "Пользователь") + ")");
                new com.example.mycompany.view.MainView(stage);
        } else {
                loginView.showError("Нет такого пользователя или неверный пароль");
                passwordField.clear();
            }
        } catch (Exception e) {
            loginView.showError("Ошибка подключения к базе данных");
            passwordField.clear();
        }
    }

    @FXML
    private void onShowRegister() {
        try{
            Stage stage = (Stage) usernameField.getScene().getWindow();
            new com.example.mycompany.view.RegisterView(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 