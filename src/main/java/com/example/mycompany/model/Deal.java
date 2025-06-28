package com.example.mycompany.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.List;

public class Deal {
    private final IntegerProperty id;
    private final StringProperty customerName;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty type; // "Опт" или "Розница"
    private final DoubleProperty discount;
    private final DoubleProperty total;
    private List<DealProduct> products;

    public Deal(int id, String customerName, LocalDate date, String type, double discount, double total) {
        this(id, customerName, date, type, discount, total, null);
    }

    public Deal(int id, String customerName, LocalDate date, String type, double discount, double total, List<DealProduct> products) {
        this.id = new SimpleIntegerProperty(id);
        this.customerName = new SimpleStringProperty(customerName);
        this.date = new SimpleObjectProperty<>(date);
        this.type = new SimpleStringProperty(type);
        this.discount = new SimpleDoubleProperty(discount);
        this.total = new SimpleDoubleProperty(total);
        this.products = products;
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getCustomerName() { return customerName.get(); }
    public LocalDate getDate() { return date.get(); }
    public String getType() { return type.get(); }

    public List<DealProduct> getProducts() { return products; }

    public String getDiscountString() {
        return String.format("%.0f%%", discount.get() * 100);
    }
    public String getTotalString() {
        return String.format("%.2f", total.get());
    }

    public void setDiscount(double discount) { this.discount.set(discount); }
    public void setTotal(double total) { this.total.set(total); }
} 