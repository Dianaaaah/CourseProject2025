package com.example.mycompany.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Deals extends Observable {
    private List<Deal> deals;

    public Deals() {
        deals = new ArrayList<>();
    }

    public List<Deal> getDeals() {
        return deals;
    }

    public void addDeal(Deal deal) {
        deals.add(deal);
        setChanged();
        notifyObservers();
    }

    public void removeDeal(Deal deal) {
        deals.remove(deal);
        setChanged();
        notifyObservers();
    }

    public void setDeals(List<Deal> deals) {
        this.deals = deals;
        setChanged();
        notifyObservers();
    }
} 