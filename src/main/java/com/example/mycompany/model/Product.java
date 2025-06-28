package com.example.mycompany.model;

import javafx.beans.property.*;

public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final DoubleProperty wholesalePrice;
    private final DoubleProperty retailPrice;
    private final StringProperty description;

    public Product(int id, String name, double wholesalePrice, double retailPrice, String description) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.wholesalePrice = new SimpleDoubleProperty(wholesalePrice);
        this.retailPrice = new SimpleDoubleProperty(retailPrice);
        this.description = new SimpleStringProperty(description);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getName() { return name.get(); }
    public double getWholesalePrice() { return wholesalePrice.get(); }
    public double getRetailPrice() { return retailPrice.get(); }
    public String getDescription() { return description.get(); }
} 