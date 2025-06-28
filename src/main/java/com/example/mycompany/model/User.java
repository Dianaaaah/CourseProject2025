package com.example.mycompany.model;

public class User {
    private int id;
    private String username;
    private String password;
    private int roleId;
    private String roleName;

    public User(int id, String username, String password, int roleId, String roleName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public User(int id, String username, String password, int roleId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRoleName() { return roleName; }

    public void setId(int id) { this.id = id; }

} 