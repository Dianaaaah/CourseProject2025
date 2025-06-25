package com.example.mycompany.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Discounts extends Observable {
    private List<Discount> discounts;

    public Discounts() {
        discounts = new ArrayList<>();
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void addDiscount(Discount discount) {
        discounts.add(discount);
        setChanged();
        notifyObservers();
    }

    public void removeDiscount(Discount discount) {
        discounts.remove(discount);
        setChanged();
        notifyObservers();
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
        setChanged();
        notifyObservers();
    }
} 