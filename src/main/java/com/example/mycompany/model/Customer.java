package com.example.mycompany.model;

import javafx.beans.property.*;

public class Customer {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty address;
    private final StringProperty phoneNumber;
    private final StringProperty contactPerson;

    public Customer(int id, String name, String address, String phoneNumber, String contactPerson) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.contactPerson = new SimpleStringProperty(contactPerson);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public String getName() { return name.get(); }
    public String getAddress() { return address.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getContactPerson() { return contactPerson.get(); }
} 