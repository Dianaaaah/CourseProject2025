package com.example.mycompany.controller;

import com.example.mycompany.dao.CustomerDAO;
import com.example.mycompany.dao.ProductDAO;
import com.example.mycompany.model.Customer;
import com.example.mycompany.model.Product;
import com.example.mycompany.model.DealProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import com.example.mycompany.model.Deal;
import java.time.LocalDate;
import com.example.mycompany.dao.DiscountDAO;
import com.example.mycompany.model.Discount;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import java.util.List;

public class DealEditDialogController {
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<String> dealTypeComboBox;
    @FXML private TableView<DealProduct> dealProductsTable;
    @FXML private TableColumn<DealProduct, String> productNameColumn;
    @FXML private TableColumn<DealProduct, Integer> quantityColumn;
    @FXML private TableColumn<DealProduct, Double> priceColumn;
    @FXML private TableColumn<DealProduct, Double> sumColumn;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    @FXML private Label dialogTitleLabel;
    @FXML private Button deleteProductButton;

    private ObservableList<DealProduct> dealProducts = FXCollections.observableArrayList();
    private Deal editingDeal;

    @FXML
    public void initialize() {
        try {
            // Покупатели
            CustomerDAO customerDAO = new CustomerDAO();
            ObservableList<Customer> customers = FXCollections.observableArrayList(customerDAO.getAllCustomers());
            customerComboBox.setItems(customers);
            customerComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Customer customer) {
                    return customer == null ? "" : customer.getName();
                }
                @Override
                public Customer fromString(String string) {
                    return customers.stream().filter(c -> c.getName().equals(string)).findFirst().orElse(null);
                }
            });
            // Тип сделки
            dealTypeComboBox.setItems(FXCollections.observableArrayList("wholesale", "retail"));
            dealTypeComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(String type) {
                    return type == null ? "" : (type.equals("wholesale") ? "Опт" : "Розница");
                }
                @Override
                public String fromString(String string) {
                    if ("Опт".equals(string)) return "wholesale";
                    if ("Розница".equals(string)) return "retail";
                    return string;
                }
            });
            // Товары
            ProductDAO productDAO = new ProductDAO();
            ObservableList<Product> products = FXCollections.observableArrayList(productDAO.getAllProducts());
            productComboBox.setItems(products);
            productComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Product product) {
                    return product == null ? "" : product.getName();
                }
                @Override
                public Product fromString(String string) {
                    return products.stream().filter(p -> p.getName().equals(string)).findFirst().orElse(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Таблица товаров в сделке
        productNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProduct().getName()));
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        sumColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getSum()).asObject());
        dealProductsTable.setItems(dealProducts);
        dealProductsTable.setEditable(true);
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityColumn.setOnEditCommit(event -> {
            DealProduct dp = event.getRowValue();
            dp.setQuantity(event.getNewValue());
            dp.setSum(dp.getPrice() * dp.getQuantity());
            recalcPrices();
        });
        // Пересчет при смене типа
        dealTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> recalcPrices());
        // Кнопка удаления товара
        if (deleteProductButton != null) {
            deleteProductButton.setOnAction(e -> onDeleteProductFromDeal());
        }
    }

    @FXML
    private void onAddProductToDeal() {
        Product product = productComboBox.getValue();
        if (product == null) {
            return;
        }
        
        // Проверка на дубликаты
        for (DealProduct existingProduct : dealProducts) {
            if (existingProduct.getProduct().getId() == product.getId()) {
                // Можно показать Alert пользователю
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Дубликат товара");
                alert.setHeaderText("Товар уже добавлен");
                alert.setContentText("Товар '" + product.getName() + "' уже добавлен в сделку. Удалите существующий товар или выберите другой.");
                alert.showAndWait();
                return;
            }
        }
        
        String type = dealTypeComboBox.getValue();
        if (type == null) {
            return;
        }
        
        String quantityText = quantityField.getText().trim();
        if (quantityText.isEmpty()) {
            return;
        }
        
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                return;
            }
        } catch (NumberFormatException e) {
            return;
        }
        
        double price = type.equals("wholesale") ? product.getWholesalePrice() : product.getRetailPrice();
        double sum = price * quantity;
        
        DealProduct newDealProduct = new DealProduct(product, quantity, price, sum);
        dealProducts.add(newDealProduct);
        
        updateTotalAndDiscount();
        productComboBox.getSelectionModel().clearSelection();
        quantityField.clear();
    }

    private void recalcPrices() {
        String type = dealTypeComboBox.getValue();
        for (DealProduct dp : dealProducts) {
            double price = type.equals("wholesale") ? dp.getProduct().getWholesalePrice() : dp.getProduct().getRetailPrice();
            dp.setPrice(price);
            dp.setSum(price * dp.getQuantity());
        }
        dealProductsTable.refresh();
        updateTotalAndDiscount();
    }

    private void updateTotalAndDiscount() {
        double total = dealProducts.stream().mapToDouble(DealProduct::getSum).sum();
        int amount = dealProducts.stream().mapToInt(DealProduct::getQuantity).sum();
        
        // Загружаем все скидки один раз и находим лучшую
        DiscountDAO discountDAO = new DiscountDAO();
        List<Discount> allDiscounts = discountDAO.getAllDiscountsForDeals();
        
        Discount bestDiscount = null;
        for (Discount discount : allDiscounts) {
            if (discount.getMinTotalCost() <= total && discount.getMinTotalAmount() <= amount) {
                if (bestDiscount == null || discount.getPercent() > bestDiscount.getPercent()) {
                    bestDiscount = discount;
                }
            }
        }
        
        double percent = (bestDiscount != null) ? bestDiscount.getPercent() : 0.0;
        double totalWithDiscount = total * (1 - percent / 100.0);
        
        discountLabel.setText("Скидка: " + (int)percent + "%");
        totalLabel.setText(String.format("Итого: %,.2f", totalWithDiscount));
    }

    public Deal getDeal() {
        Customer customer = customerComboBox.getValue();
        String type = dealTypeComboBox.getValue();
        LocalDate date = editingDeal != null ? editingDeal.getDate() : LocalDate.now();
        
        double total = dealProducts.stream().mapToDouble(DealProduct::getSum).sum();
        int amount = dealProducts.stream().mapToInt(DealProduct::getQuantity).sum();
        
        // Загружаем все скидки один раз и находим лучшую
        DiscountDAO discountDAO = new DiscountDAO();
        List<Discount> allDiscounts = discountDAO.getAllDiscountsForDeals();
        
        Discount bestDiscount = null;
        for (Discount discount : allDiscounts) {
            if (discount.getMinTotalCost() <= total && discount.getMinTotalAmount() <= amount) {
                if (bestDiscount == null || discount.getPercent() > bestDiscount.getPercent()) {
                    bestDiscount = discount;
                }
            }
        }
        
        double percent = (bestDiscount != null) ? bestDiscount.getPercent() : 0.0;
        double totalWithDiscount = total * (1 - percent / 100.0);
        
        Deal result = new Deal(
            editingDeal != null ? editingDeal.getId() : 0,
            customer != null ? customer.getName() : "",
            date,
            type,
            percent / 100.0,
            totalWithDiscount,
            new java.util.ArrayList<>(dealProducts)
        );
        
        return result;
    }

    public void setDeal(Deal deal) {
        this.editingDeal = deal;
        // Установить значения в UI
        for (Customer c : customerComboBox.getItems()) {
            if (c.getName().equals(deal.getCustomerName())) {
                customerComboBox.setValue(c);
                break;
            }
        }
        dealTypeComboBox.setValue(deal.getType());
        dealProducts.setAll(deal.getProducts() != null ? deal.getProducts() : java.util.Collections.emptyList());
        updateTotalAndDiscount();
    }

    public int getSelectedCustomerId() {
        Customer customer = customerComboBox.getValue();
        return customer != null ? customer.getId() : 0;
    }

    public void setDialogTitle(String title) {
        if (dialogTitleLabel != null) dialogTitleLabel.setText(title);
    }

    @FXML
    private void onDeleteProductFromDeal() {
        DealProduct selected = dealProductsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dealProducts.remove(selected);
            updateTotalAndDiscount();
        }
    }
} 