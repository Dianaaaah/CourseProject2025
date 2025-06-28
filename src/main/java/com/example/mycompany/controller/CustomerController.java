package com.example.mycompany.controller;

import com.example.mycompany.dao.CustomerDAO;
import com.example.mycompany.model.Customer;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class CustomerController {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> contactColumn;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        loadCustomers();
    }

    private void loadCustomers() {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            ObservableList<Customer> customers = FXCollections.observableArrayList(customerDAO.getAllCustomers());
            customerTable.setItems(customers);
        } catch (Exception e) {
            showMessage("Ошибка загрузки клиентов: " + e.getMessage(), false);
        }
    }

    @FXML
    private void onAddCustomer() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Добавить клиента");

        Label header = new Label("Добавить клиента");
        header.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2a3b8f;" +
            "-fx-padding: 0 0 18 0;" +
            "-fx-alignment: center;"
        );
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Имя:");
        TextField nameField = new TextField();
        nameField.setPromptText("Введите имя");
        nameField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label addressLabel = new Label("Адрес:");
        TextField addressField = new TextField();
        addressField.setPromptText("Введите адрес");
        addressField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label phoneLabel = new Label("Телефон:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Введите телефон");
        phoneField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label contactLabel = new Label("Email:");
        TextField contactField = new TextField();
        contactField.setPromptText("Введите email");
        contactField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, #b0b8d1, 12, 0.2, 0, 4);");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(addressLabel, 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(contactLabel, 0, 3);
        grid.add(contactField, 1, 3);

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
                    return new Customer(
                        0,
                        nameField.getText(),
                        addressField.getText(),
                        phoneField.getText(),
                        contactField.getText()
                    );
                } catch (Exception e) {
                    showMessage("Ошибка: некорректные данные", false);
                    return null;
                }
            }
            return null;
        });

        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(customer -> {
            try {
                new CustomerDAO().addCustomer(customer);
                showMessage("Клиент добавлен!", true);
                loadCustomers();
            } catch (Exception e) {
                showMessage("Ошибка: " + e.getMessage(), false);
            }
        });
    }

    @FXML
    private void onEditCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите клиента для изменения", false);
            return;
        }
        
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Изменить клиента");

        Label header = new Label("Изменить клиента");
        header.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2a3b8f;" +
            "-fx-padding: 0 0 18 0;" +
            "-fx-alignment: center;"
        );
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Имя:");
        TextField nameField = new TextField(selected.getName());
        nameField.setPromptText("Введите имя");
        nameField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label addressLabel = new Label("Адрес:");
        TextField addressField = new TextField(selected.getAddress());
        addressField.setPromptText("Введите адрес");
        addressField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label phoneLabel = new Label("Телефон:");
        TextField phoneField = new TextField(selected.getPhoneNumber());
        phoneField.setPromptText("Введите телефон");
        phoneField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label contactLabel = new Label("Email:");
        TextField contactField = new TextField(selected.getContactPerson());
        contactField.setPromptText("Введите email");
        contactField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, #b0b8d1, 12, 0.2, 0, 4);");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(addressLabel, 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(contactLabel, 0, 3);
        grid.add(contactField, 1, 3);

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
                    return new Customer(
                        selected.getId(),
                        nameField.getText(),
                        addressField.getText(),
                        phoneField.getText(),
                        contactField.getText()
                    );
                } catch (Exception e) {
                    showMessage("Ошибка: некорректные данные", false);
                    return null;
                }
            }
            return null;
        });

        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(customer -> {
            try {
                new CustomerDAO().updateCustomer(customer);
                showMessage("Клиент изменён!", true);
                loadCustomers();
            } catch (Exception e) {
                showMessage("Ошибка: " + e.getMessage(), false);
            }
        });
    }

    @FXML
    private void onDeleteCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите клиента для удаления", false);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить клиента?");
        alert.setContentText("Клиент '" + selected.getName() + "' будет удалён безвозвратно.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                new CustomerDAO().deleteCustomer(selected.getId());
                showMessage("Клиент удалён!", true);
                loadCustomers();
            } catch (Exception e) {
                showMessage("Ошибка: " + e.getMessage(), false);
            }
        }
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setVisible(true);
    }
} 