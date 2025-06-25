package com.example.mycompany.model;

public class Product {
    private int id;
    private String name;
    private double wholesalePrice;
    private double retailPrice;
    private String description;

    public Product(int id, String name, double wholesalePrice, double retailPrice, String description) {
        this.id = id;
        this.name = name;
        this.wholesalePrice = wholesalePrice;
        this.retailPrice = retailPrice;
        this.description = description;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getWholesalePrice() { return wholesalePrice; }
    public double getRetailPrice() { return retailPrice; }
    public String getDescription() { return description; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setWholesalePrice(double wholesalePrice) { this.wholesalePrice = wholesalePrice; }
    public void setRetailPrice(double retailPrice) { this.retailPrice = retailPrice; }
    public void setDescription(String description) { this.description = description; }
} 