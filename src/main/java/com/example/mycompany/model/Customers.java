package com.example.mycompany.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Customers extends Observable {
    private List<Customer> customers;

    public Customers() {
        customers = new ArrayList<>();
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        setChanged();
        notifyObservers();
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
        setChanged();
        notifyObservers();
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        setChanged();
        notifyObservers();
    }
} 