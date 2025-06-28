package com.example.mycompany.model;

public class DealProduct {
    private Product product;
    private int quantity;
    private double price;
    private double sum;

    public DealProduct(Product product, int quantity, double price, double sum) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.sum = sum;
    }

    public Product getProduct() {
        return product;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public double getSum() {
        return sum;
    }
    public void setSum(double sum) {
        this.sum = sum;
    }
} 