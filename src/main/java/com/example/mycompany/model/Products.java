package com.example.mycompany.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Products extends Observable {
    private List<Product> products;

    public Products() {
        products = new ArrayList<>();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
        setChanged();
        notifyObservers();
    }

    public void removeProduct(Product product) {
        products.remove(product);
        setChanged();
        notifyObservers();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        setChanged();
        notifyObservers();
    }
} 