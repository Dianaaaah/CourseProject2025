package com.example.mycompany.model;

public class DealProduct {
    private int dealId;
    private int productId;
    private double productPrice;
    private int amount;

    public DealProduct(int dealId, int productId, double productPrice, int amount) {
        this.dealId = dealId;
        this.productId = productId;
        this.productPrice = productPrice;
        this.amount = amount;
    }

    public int getDealId() { return dealId; }
    public int getProductId() { return productId; }
    public double getProductPrice() { return productPrice; }
    public int getAmount() { return amount; }

    public void setDealId(int dealId) { this.dealId = dealId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }
    public void setAmount(int amount) { this.amount = amount; }
} 