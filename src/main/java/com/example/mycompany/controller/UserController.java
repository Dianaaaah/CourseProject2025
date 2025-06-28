package com.example.mycompany.controller;

import com.example.mycompany.dao.UserDAO;
import com.example.mycompany.model.User;
import com.example.mycompany.model.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class UserController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        try {
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
            loadUsers();
        } catch (SecurityException e) {
            showMessage("У вас нет прав для просмотра пользователей", false);
            userTable.setVisible(false);
        }
    }

    private void loadUsers() {
        try {
            UserDAO userDAO = UserDAO.getInstance();
            User currentUser = new User(
                com.example.mycompany.model.Session.currentUserId,
                com.example.mycompany.model.Session.currentUsername,
                null,
                com.example.mycompany.model.Session.currentRole.equals("admin") ? 1 : 2,
                com.example.mycompany.model.Session.currentRole
            );
            ObservableList<User> users = FXCollections.observableArrayList(userDAO.getAllUsersWithRoles(currentUser));
            userTable.setItems(users);
        } catch (SecurityException e) {
            showMessage("У вас нет прав для просмотра пользователей", false);
            userTable.setVisible(false);
        }
    }

    @FXML
    private void onAddUser() {
        if (!Session.isAdmin()) {
            showMessage("У вас нет прав для добавления пользователей", false);
            return;
        }
        
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Добавить пользователя");

        Label header = new Label("Добавить пользователя");
        header.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2a3b8f;" +
            "-fx-padding: 0 0 18 0;" +
            "-fx-alignment: center;"
        );
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Имя пользователя:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Введите имя пользователя");
        usernameField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label passwordLabel = new Label("Пароль:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Введите пароль");
        passwordField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label roleLabel = new Label("Роль:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("user", "admin");
        roleComboBox.setValue("user");
        roleComboBox.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, #b0b8d1, 12, 0.2, 0, 4);");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(roleLabel, 0, 2);
        grid.add(roleComboBox, 1, 2);

        VBox vbox = new VBox(10, header, grid);
        vbox.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(vbox);

        ButtonType okButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButtonType, cancelButtonType);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setStyle("-fx-background-color: #2a3b8f; -fx-text-fill: white; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #e0e7ef; -fx-text-fill: #2a3b8f; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    String username = usernameField.getText().trim();
                    String password = passwordField.getText().trim();
                    String role = roleComboBox.getValue();
                    
                    if (username.isEmpty() || password.isEmpty()) {
                        showMessage("Заполните все поля", false);
                        return null;
                    }
                    
                    UserDAO userDAO = UserDAO.getInstance();
                    if (userDAO.addUser(username, password, role)) {
                        return new User(0, username, password, role.equals("admin") ? 1 : 2, role);
                    } else {
                        showMessage("Ошибка добавления пользователя", false);
                        return null;
                    }
                } catch (Exception e) {
                    showMessage("Ошибка: " + e.getMessage(), false);
                    return null;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            showMessage("Пользователь добавлен!", true);
            loadUsers();
        });
    }

    @FXML
    private void onEditUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите пользователя для изменения", false);
            return;
        }
        
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Изменить пользователя");

        Label header = new Label("Изменить пользователя");
        header.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2a3b8f;" +
            "-fx-padding: 0 0 18 0;" +
            "-fx-alignment: center;"
        );
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Имя пользователя:");
        TextField usernameField = new TextField(selected.getUsername());
        usernameField.setPromptText("Введите имя пользователя");
        usernameField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label passwordLabel = new Label("Новый пароль:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Оставьте пустым, если не хотите менять");
        passwordField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label roleLabel = new Label("Роль:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("user", "admin");
        roleComboBox.setValue(selected.getRoleName());
        roleComboBox.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, #b0b8d1, 12, 0.2, 0, 4);");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(roleLabel, 0, 2);
        grid.add(roleComboBox, 1, 2);

        VBox vbox = new VBox(10, header, grid);
        vbox.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(vbox);

        ButtonType okButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(okButtonType, cancelButtonType);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setStyle("-fx-background-color: #2a3b8f; -fx-text-fill: white; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #e0e7ef; -fx-text-fill: #2a3b8f; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    return new User(
                        selected.getId(),
                        usernameField.getText(),
                        passwordField.getText().isEmpty() ? selected.getPassword() : passwordField.getText(),
                        roleComboBox.getValue().equals("admin") ? 1 : 2,
                        roleComboBox.getValue()
                    );
                } catch (Exception e) {
                    showMessage("Ошибка: некорректные данные", false);
                    return null;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            try {
                UserDAO userDAO = UserDAO.getInstance();
                userDAO.updateUser(user.getId(), user.getUsername(), user.getPassword(), user.getRoleName());
                showMessage("Пользователь изменён!", true);
                loadUsers();
            } catch (Exception e) {
                showMessage("Ошибка: " + e.getMessage(), false);
            }
        });
    }

    @FXML
    private void onDeleteUser() {
        if (!Session.isAdmin()) {
            showMessage("У вас нет прав для удаления пользователей", false);
            return;
        }
        
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите пользователя для удаления", false);
            return;
        }
        
        // Нельзя удалить самого себя
        if (selected.getId() == Session.currentUserId) {
            showMessage("Нельзя удалить самого себя", false);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить пользователя?");
        alert.setContentText("Пользователь '" + selected.getUsername() + "' будет удалён безвозвратно.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                UserDAO userDAO = UserDAO.getInstance();
                if (userDAO.deleteUser(selected.getId())) {
                    showMessage("Пользователь удалён!", true);
                    loadUsers();
                } else {
                    showMessage("Ошибка удаления пользователя", false);
                }
            } catch (Exception e) {
                showMessage("Ошибка: " + e.getMessage(), false);
            }
        }
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setStyle(success ? "-fx-text-fill: #2a3b8f;" : "-fx-text-fill: red;");
        messageLabel.setVisible(true);
    }
} 