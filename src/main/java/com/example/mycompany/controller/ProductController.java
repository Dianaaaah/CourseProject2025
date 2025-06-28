package com.example.mycompany.controller;

import com.example.mycompany.dao.ProductDAO;
import com.example.mycompany.model.Product;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

public class ProductController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Number> wholesalePriceColumn;
    @FXML private TableColumn<Product, Number> retailPriceColumn;
    @FXML private TableColumn<Product, String> descriptionColumn;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        wholesalePriceColumn.setCellValueFactory(new PropertyValueFactory<>("wholesalePrice"));
        retailPriceColumn.setCellValueFactory(new PropertyValueFactory<>("retailPrice"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(tc -> {
            TableCell<Product, String> cell = new TableCell<>() {
                private final Text text = new Text();
                {
                    text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
                    setGraphic(text);
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    text.setText(empty || item == null ? "" : item);
                }
            };
            return cell;
        });
        
        loadProducts();
    }

    private void loadProducts() {
        try {
            ProductDAO productDAO = new ProductDAO();
            ObservableList<Product> products = FXCollections.observableArrayList(productDAO.getAllProducts());
            productTable.setItems(products);
        } catch (Exception e) {
            showMessage("Ошибка загрузки товаров: " + e.getMessage(), false);
        }
    }

    @FXML
    private void onAddProduct() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Добавить товар");

        Label header = new Label("Добавить");
        header.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2a3b8f;" +
            "-fx-padding: 0 0 18 0;" +
            "-fx-alignment: center;"
        );
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Название:");
        TextField nameField = new TextField();
        nameField.setPromptText("Введите название");
        nameField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label wholesaleLabel = new Label("Опт. цена:");
        TextField wholesaleField = new TextField();
        wholesaleField.setPromptText("0.00");
        wholesaleField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label retailLabel = new Label("Розн. цена:");
        TextField retailField = new TextField();
        retailField.setPromptText("0.00");
        retailField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label descLabel = new Label("Описание:");
        TextField descField = new TextField();
        descField.setPromptText("Описание товара");
        descField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, #b0b8d1, 12, 0.2, 0, 4);");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(wholesaleLabel, 0, 1);
        grid.add(wholesaleField, 1, 1);
        grid.add(retailLabel, 0, 2);
        grid.add(retailField, 1, 2);
        grid.add(descLabel, 0, 3);
        grid.add(descField, 1, 3);

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
                    return new Product(
                        0,
                        nameField.getText(),
                        Double.parseDouble(wholesaleField.getText()),
                        Double.parseDouble(retailField.getText()),
                        descField.getText()
                    );
                } catch (Exception e) {
                    showMessage("Ошибка: некорректные данные", false);
                    return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            try {
                new ProductDAO().addProduct(product);
                showMessage("Товар добавлен!", true);
                loadProducts();
            } catch (Exception e) {
                showMessage("Ошибка: " + e.getMessage(), false);
            }
        });
    }

    @FXML
    private void onEditProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите товар для изменения", false);
            return;
        }
        
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Изменить товар");

        Label header = new Label("Изменить");
        header.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2a3b8f;" +
            "-fx-padding: 0 0 18 0;" +
            "-fx-alignment: center;"
        );
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Название:");
        TextField nameField = new TextField(selected.getName());
        nameField.setPromptText("Введите название");
        nameField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label wholesaleLabel = new Label("Опт. цена:");
        TextField wholesaleField = new TextField(String.valueOf(selected.getWholesalePrice()));
        wholesaleField.setPromptText("0.00");
        wholesaleField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label retailLabel = new Label("Розн. цена:");
        TextField retailField = new TextField(String.valueOf(selected.getRetailPrice()));
        retailField.setPromptText("0.00");
        retailField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label descLabel = new Label("Описание:");
        TextField descField = new TextField(selected.getDescription());
        descField.setPromptText("Описание товара");
        descField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setStyle("-fx-background-color: #fff; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, #b0b8d1, 12, 0.2, 0, 4);");
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(wholesaleLabel, 0, 1);
        grid.add(wholesaleField, 1, 1);
        grid.add(retailLabel, 0, 2);
        grid.add(retailField, 1, 2);
        grid.add(descLabel, 0, 3);
        grid.add(descField, 1, 3);

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
                    return new Product(
                        selected.getId(),
                        nameField.getText(),
                        Double.parseDouble(wholesaleField.getText()),
                        Double.parseDouble(retailField.getText()),
                        descField.getText()
                    );
                } catch (Exception e) {
                    showMessage("Ошибка: некорректные данные", false);
                    return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            try {
                new ProductDAO().updateProduct(product);
                showMessage("Товар изменён!", true);
                loadProducts();
            } catch (Exception e) {
                showMessage("Ошибка: " + e.getMessage(), false);
            }
        });
    }

    @FXML
    private void onDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите товар для удаления", false);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить товар?");
        alert.setContentText("Товар '" + selected.getName() + "' будет удалён безвозвратно.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                new ProductDAO().deleteProduct(selected.getId());
                showMessage("Товар удалён!", true);
                loadProducts();
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