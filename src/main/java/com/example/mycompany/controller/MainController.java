package com.example.mycompany.controller;

import com.example.mycompany.model.Session;
import com.example.mycompany.model.User;
import com.example.mycompany.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import java.util.Optional;

public class MainController {
    @FXML private Button productsButton, customersButton, dealsButton, usersButton, discountsButton, profileButton;
    @FXML private Label userInfoLabel;
    @FXML private Pane contentPane;

    @FXML
    public void initialize() {
        // Показываем информацию о пользователе
        updateUserInfo();
        
        // Настраиваем кнопки навигации
        productsButton.setOnAction(e -> loadView("/com/example/mycompany/product-view.fxml"));
        customersButton.setOnAction(e -> loadView("/com/example/mycompany/customer-view.fxml"));
        dealsButton.setOnAction(e -> loadView("/com/example/mycompany/deal-view.fxml"));
        
        // Кнопки только для администратора
        if (Session.isAdmin()) {
            usersButton.setVisible(true);
            discountsButton.setVisible(true);
            usersButton.setOnAction(e -> loadView("/com/example/mycompany/user-view.fxml"));
            discountsButton.setOnAction(e -> loadView("/com/example/mycompany/discount-view.fxml"));
        } else {
            usersButton.setVisible(false);
            discountsButton.setVisible(false);
        }
        
        // Загружаем продукты по умолчанию
        loadView("/com/example/mycompany/product-view.fxml");
    }
    
    private void updateUserInfo() {
        String roleText = Session.isAdmin() ? "Администратор" : "Пользователь";
        userInfoLabel.setText(Session.currentUsername + " (" + roleText + ")");
    }

    @FXML
    private void onProfileEdit() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Редактирование профиля");

        // Заголовок
        Label header = new Label("Редактирование профиля");
        header.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2a3b8f;" +
            "-fx-padding: 0 0 18 0;" +
            "-fx-alignment: center;"
        );
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        // Поля ввода
        Label usernameLabel = new Label("Имя пользователя:");
        TextField usernameField = new TextField(Session.currentUsername);
        usernameField.setPromptText("Введите новое имя пользователя");
        usernameField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label currentPasswordLabel = new Label("Текущий пароль:");
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Введите текущий пароль");
        currentPasswordField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label newPasswordLabel = new Label("Новый пароль:");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Введите новый пароль (оставьте пустым, если не хотите менять)");
        newPasswordField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label confirmPasswordLabel = new Label("Подтвердите пароль:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Повторите новый пароль");
        confirmPasswordField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        // Сетка
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, #b0b8d1, 12, 0.2, 0, 4);");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(currentPasswordLabel, 0, 1);
        grid.add(currentPasswordField, 1, 1);
        grid.add(newPasswordLabel, 0, 2);
        grid.add(newPasswordField, 1, 2);
        grid.add(confirmPasswordLabel, 0, 3);
        grid.add(confirmPasswordField, 1, 3);

        VBox vbox = new VBox(10, header, grid);
        vbox.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(vbox);

        // Кнопки
        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(saveButtonType, cancelButtonType);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setStyle("-fx-background-color: #2a3b8f; -fx-text-fill: white; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #e0e7ef; -fx-text-fill: #2a3b8f; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");

        // Обработка результата
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String newUsername = usernameField.getText().trim();
                    String currentPassword = currentPasswordField.getText();
                    String newPassword = newPasswordField.getText();
                    String confirmPassword = confirmPasswordField.getText();

                    // Валидация
                    if (newUsername.isEmpty()) {
                        showAlert("Ошибка", "Имя пользователя не может быть пустым", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (currentPassword.isEmpty()) {
                        showAlert("Ошибка", "Введите текущий пароль", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Проверяем текущий пароль
                    UserDAO userDAO = UserDAO.getInstance();
                    User authenticatedUser = userDAO.authenticate(Session.currentUsername, currentPassword);
                    if (authenticatedUser == null) {
                        showAlert("Ошибка", "Неверный текущий пароль", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Если введен новый пароль, проверяем его
                    if (!newPassword.isEmpty()) {
                        if (!newPassword.equals(confirmPassword)) {
                            showAlert("Ошибка", "Пароли не совпадают", Alert.AlertType.ERROR);
                            return null;
                        }
                        if (newPassword.length() < 3) {
                            showAlert("Ошибка", "Новый пароль должен содержать минимум 3 символа", Alert.AlertType.ERROR);
                            return null;
                        }
                    }

                    // Обновляем профиль
                    boolean success = userDAO.updateProfile(Session.currentUserId, newUsername, newPassword.isEmpty() ? null : newPassword);
                    if (success) {
                        // Обновляем сессию
                        Session.currentUsername = newUsername;
                        updateUserInfo();
                        showAlert("Успех", "Профиль успешно обновлен", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Ошибка", "Не удалось обновить профиль", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showAlert("Ошибка", "Произошла ошибка: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadView(String fxmlPath) {
        try {
            Pane view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 