package com.example.mycompany.model;

import java.sql.Date;

public class Deal {
    private int id;
    private int customerId;
    private Date dealDate;
    private String dealType;
    private Integer discountId; // Может быть null

    public Deal(int id, int customerId, Date dealDate, String dealType, Integer discountId) {
        this.id = id;
        this.customerId = customerId;
        this.dealDate = dealDate;
        this.dealType = dealType;
        this.discountId = discountId;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public Date getDealDate() { return dealDate; }
    public String getDealType() { return dealType; }
    public Integer getDiscountId() { return discountId; }

    public void setId(int id) { this.id = id; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setDealDate(Date dealDate) { this.dealDate = dealDate; }
    public void setDealType(String dealType) { this.dealType = dealType; }
    public void setDiscountId(Integer discountId) { this.discountId = discountId; }
} 