package com.example.mycompany.controller;

import com.example.mycompany.dao.DiscountDAO;
import com.example.mycompany.model.Discount;
import com.example.mycompany.model.Session;
import com.example.mycompany.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.Optional;

public class DiscountController {
    @FXML private TableView<Discount> discountTable;
    @FXML private TableColumn<Discount, Number> percentColumn;
    @FXML private TableColumn<Discount, Number> minTotalCostColumn;
    @FXML private TableColumn<Discount, Number> minTotalAmountColumn;
    @FXML private Button addButton, editButton, deleteButton;
    @FXML private Label messageLabel;
    @FXML private VBox rootVBox;

    private final DiscountDAO discountDAO = new DiscountDAO();
    private ObservableList<Discount> discounts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            percentColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPercent()));
            minTotalCostColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getMinTotalCost()));
            minTotalAmountColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getMinTotalAmount()));
            refreshDiscounts();
        } catch (SecurityException e) {
            showMessage("У вас нет прав для просмотра скидок", false);
            discountTable.setVisible(false);
        }
    }

    private void refreshDiscounts() {
        try {
            DiscountDAO discountDAO = new DiscountDAO();
            User currentUser = new User(
                com.example.mycompany.model.Session.currentUserId,
                com.example.mycompany.model.Session.currentUsername,
                null,
                com.example.mycompany.model.Session.currentRole.equals("admin") ? 1 : 2,
                com.example.mycompany.model.Session.currentRole
            );
            discounts.setAll(discountDAO.getAllDiscounts(currentUser));
            discountTable.setItems(discounts);
        } catch (SecurityException e) {
            showMessage("У вас нет прав для просмотра скидок", false);
            discountTable.setVisible(false);
        }
    }

    @FXML
    private void onAddDiscount() {
        if (!Session.isAdmin()) {
            showMessage("У вас нет прав для добавления скидок", false);
            return;
        }
        
        Dialog<Discount> dialog = new Dialog<>();
        dialog.setTitle("Добавить скидку");
        dialog.setHeaderText(null);
        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);
        
        // Стилизация кнопок
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okType);
        okButton.setStyle("-fx-background-color: #2a3b8f; -fx-text-fill: white; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #e0e7ef; -fx-text-fill: #2a3b8f; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
        
        TextField percentField = new TextField();
        TextField minCostField = new TextField();
        TextField minAmountField = new TextField();
        VBox vbox = new VBox(8, new Label("% скидки:"), percentField, new Label("Мин. сумма:"), minCostField, new Label("Мин. кол-во:"), minAmountField);
        dialog.getDialogPane().setContent(vbox);
        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                try {
                    double percent = Double.parseDouble(percentField.getText());
                    double minCost = Double.parseDouble(minCostField.getText());
                    int minAmount = Integer.parseInt(minAmountField.getText());
                    return new Discount(0, percent, minCost, minAmount);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(discount -> {
            if (discountDAO.addDiscount(discount.getPercent(), discount.getMinTotalCost(), discount.getMinTotalAmount())) {
                showMessage("Скидка добавлена", true);
                refreshDiscounts();
            } else {
                showMessage("Ошибка добавления скидки", false);
            }
        });
    }

    @FXML
    private void onEditDiscount() {
        if (!Session.isAdmin()) {
            showMessage("У вас нет прав для редактирования скидок", false);
            return;
        }
        
        Discount selected = discountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите скидку для изменения", false);
            return;
        }
        
        Dialog<Discount> dialog = new Dialog<>();
        dialog.setTitle("Изменить скидку");
        dialog.setHeaderText(null);
        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);
        
        // Стилизация кнопок
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okType);
        okButton.setStyle("-fx-background-color: #2a3b8f; -fx-text-fill: white; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #e0e7ef; -fx-text-fill: #2a3b8f; -fx-font-size: 15px; -fx-background-radius: 8; -fx-padding: 8 24; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
        
        TextField percentField = new TextField(String.valueOf(selected.getPercent()));
        TextField minCostField = new TextField(String.valueOf(selected.getMinTotalCost()));
        TextField minAmountField = new TextField(String.valueOf(selected.getMinTotalAmount()));
        VBox vbox = new VBox(8, new Label("% скидки:"), percentField, new Label("Мин. сумма:"), minCostField, new Label("Мин. кол-во:"), minAmountField);
        dialog.getDialogPane().setContent(vbox);
        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                try {
                    double percent = Double.parseDouble(percentField.getText());
                    double minCost = Double.parseDouble(minCostField.getText());
                    int minAmount = Integer.parseInt(minAmountField.getText());
                    return new Discount(selected.getId(), percent, minCost, minAmount);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(discount -> {
            if (discountDAO.updateDiscount(discount.getId(), discount.getPercent(), discount.getMinTotalCost(), discount.getMinTotalAmount())) {
                showMessage("Скидка изменена", true);
                refreshDiscounts();
            } else {
                showMessage("Ошибка изменения скидки", false);
            }
        });
    }

    @FXML
    private void onDeleteDiscount() {
        if (!Session.isAdmin()) {
            showMessage("У вас нет прав для удаления скидок", false);
            return;
        }
        
        Discount selected = discountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите скидку для удаления", false);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить скидку?");
        alert.setContentText("Скидка " + selected.getPercent() + "% будет удалена безвозвратно.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                discountDAO.deleteDiscount(selected.getId());
                showMessage("Скидка удалена!", true);
                refreshDiscounts();
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