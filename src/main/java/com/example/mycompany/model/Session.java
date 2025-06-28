package com.example.mycompany.model;

public class Session {
    public static String currentRole = "user";
    public static int currentUserId = 0;
    public static String currentUsername = "";
    public static User currentUser = null;
    
    public static boolean isAdmin() {
        return "admin".equals(currentRole);
    }
    
    public static boolean isUser() {
        return "user".equals(currentRole);
    }
    
    public static void setUser(String role, int userId, String username) {
        currentRole = role;
        currentUserId = userId;
        currentUsername = username;
        currentUser = new User(userId, username, null, "admin".equals(role) ? 1 : 2, role);
    }
    
    public static void clear() {
        currentRole = "user";
        currentUserId = 0;
        currentUsername = "";
        currentUser = null;
    }
} 