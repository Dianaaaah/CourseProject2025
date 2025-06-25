package com.example.mycompany.model;

public class User {
    private int id;
    private String username;
    private String password;
    private int roleId;
    private String fullName;

    public User(int id, String username, String password, int roleId, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getRoleId() { return roleId; }
    public String getFullName() { return fullName; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
} 