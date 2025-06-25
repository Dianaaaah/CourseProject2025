package com.example.mycompany.model;

public class Discount {
    private int id;
    private double percent;
    private double minTotalCost;
    private int minTotalAmount;

    public Discount(int id, double percent, double minTotalCost, int minTotalAmount) {
        this.id = id;
        this.percent = percent;
        this.minTotalCost = minTotalCost;
        this.minTotalAmount = minTotalAmount;
    }

    public int getId() { return id; }
    public double getPercent() { return percent; }
    public double getMinTotalCost() { return minTotalCost; }
    public int getMinTotalAmount() { return minTotalAmount; }

    public void setId(int id) { this.id = id; }
    public void setPercent(double percent) { this.percent = percent; }
    public void setMinTotalCost(double minTotalCost) { this.minTotalCost = minTotalCost; }
    public void setMinTotalAmount(int minTotalAmount) { this.minTotalAmount = minTotalAmount; }
} 