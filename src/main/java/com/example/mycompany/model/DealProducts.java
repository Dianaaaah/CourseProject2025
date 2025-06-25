package com.example.mycompany.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class DealProducts extends Observable {
    private List<DealProduct> dealProducts;

    public DealProducts() {
        dealProducts = new ArrayList<>();
    }

    public List<DealProduct> getDealProducts() {
        return dealProducts;
    }

    public void addDealProduct(DealProduct dealProduct) {
        dealProducts.add(dealProduct);
        setChanged();
        notifyObservers();
    }

    public void removeDealProduct(DealProduct dealProduct) {
        dealProducts.remove(dealProduct);
        setChanged();
        notifyObservers();
    }

    public void setDealProducts(List<DealProduct> dealProducts) {
        this.dealProducts = dealProducts;
        setChanged();
        notifyObservers();
    }
} 