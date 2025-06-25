package com.example.mycompany.model;

public class Customer {
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String contactPerson;

    public Customer(int id, String name, String address, String phoneNumber, String contactPerson) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPerson = contactPerson;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getContactPerson() { return contactPerson; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
} 