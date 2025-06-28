package com.example.mycompany.controller;

import com.example.mycompany.model.Deal;
import com.example.mycompany.dao.DealDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Optional;

public class DealController {
    @FXML private TableView<Deal> dealTable;
    @FXML private TableColumn<Deal, String> customerColumn;
    @FXML private TableColumn<Deal, String> dateColumn;
    @FXML private TableColumn<Deal, String> typeColumn;
    @FXML private TableColumn<Deal, String> discountColumn;
    @FXML private TableColumn<Deal, String> totalColumn;
    @FXML private Label messageLabel;

    private ObservableList<Deal> deals = FXCollections.observableArrayList();
    private final DealDAO dealDAO = new DealDAO();

    @FXML
    public void initialize() {
        // Настраиваем таблицу
        customerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomerName()));
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : ""
        ));
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        discountColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDiscountString()));
        totalColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTotalString()));
        dealTable.setItems(deals);
        
        loadDeals();
    }

    private void loadDeals() {
        List<Deal> loadedDeals = dealDAO.getAllDeals(true);
        deals.setAll(loadedDeals);
    }

    @FXML
    private void onAddDeal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mycompany/deal-edit-dialog.fxml"));
            VBox root = loader.load();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Создать сделку");
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            okButton.setText("Сохранить");
            okButton.setStyle("-fx-font-size: 16px; -fx-background-radius: 8; -fx-background-color: #2a3b8f; -fx-text-fill: white; -fx-padding: 8 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
            cancelButton.setText("Отмена");
            cancelButton.setStyle("-fx-font-size: 16px; -fx-background-radius: 8; -fx-background-color: #e0e7ef; -fx-text-fill: #2a3b8f; -fx-padding: 8 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
            DealEditDialogController controller = loader.getController();
            controller.setDialogTitle("Создать сделку");
            dialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    Deal newDeal = controller.getDeal();
                    int customerId = controller.getSelectedCustomerId();
                    int dealId = dealDAO.addDeal(newDeal, customerId);
                    if (dealId > 0) {
                        newDeal.setId(dealId);
                        deals.add(newDeal);
                        showMessage("Сделка добавлена", true);
                    } else {
                        showMessage("Ошибка при добавлении сделки", false);
                    }
                }
            });
        } catch (Exception e) {
            showMessage("Ошибка открытия окна сделки: " + e.getMessage(), false);
        }
    }

    @FXML
    private void onEditDeal() {
        Deal selected = dealTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите сделку для редактирования", false);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mycompany/deal-edit-dialog.fxml"));
            VBox root = loader.load();
            Deal dealWithProducts = dealDAO.getDealById(selected.getId());
            if (dealWithProducts == null) {
                showMessage("Ошибка загрузки сделки", false);
                return;
            }
            DealEditDialogController controller = loader.getController();
            controller.setDeal(dealWithProducts);
            controller.setDialogTitle("Редактировать сделку");
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Редактировать сделку");
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            okButton.setText("Сохранить");
            okButton.setStyle("-fx-font-size: 16px; -fx-background-radius: 8; -fx-background-color: #2a3b8f; -fx-text-fill: white; -fx-padding: 8 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
            cancelButton.setText("Отмена");
            cancelButton.setStyle("-fx-font-size: 16px; -fx-background-radius: 8; -fx-background-color: #e0e7ef; -fx-text-fill: #2a3b8f; -fx-padding: 8 32; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);");
            dialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    Deal updatedDeal = controller.getDeal();
                    int customerId = controller.getSelectedCustomerId();
                    if (dealDAO.updateDeal(updatedDeal, customerId)) {
                        loadDeals();
                        showMessage("Сделка обновлена", true);
                    } else {
                        showMessage("Ошибка при обновлении сделки", false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Ошибка открытия окна сделки: " + e.getMessage(), false);
        }
    }

    @FXML
    private void onDeleteDeal() {
        Deal selected = dealTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Выберите сделку для удаления", false);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить сделку?");
        alert.setContentText("Вы уверены, что хотите удалить эту сделку?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dealDAO.deleteDeal(selected.getId())) {
                    deals.remove(selected);
                    showMessage("Сделка удалена", true);
                } else {
                    showMessage("Ошибка при удалении сделки", false);
                }
            }
        });
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setStyle(success ? "-fx-text-fill: #2a3b8f;" : "-fx-text-fill: red;");
        messageLabel.setVisible(true);
    }
} 