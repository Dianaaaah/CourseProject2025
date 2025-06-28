package com.example.mycompany.dao;

import com.example.mycompany.Database;
import com.example.mycompany.model.User;
import java.sql.*;

public class UserDAO {
    private static UserDAO instance;

    private UserDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT u.id, u.username, u.password, u.role_id, r.name as role_name " +
                     "FROM users u JOIN roles r ON u.role_id = r.id " +
                     "WHERE u.username = ? AND u.password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("role_id"),
                    rs.getString("role_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String password) {
        String sql = "INSERT INTO users (username, password, role_id) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, 2);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<User> getAllUsersWithRoles(com.example.mycompany.model.User currentUser) {
        if (!"admin".equals(currentUser.getRoleName())) {
            throw new SecurityException("Нет доступа");
        }
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password, u.role_id, r.name as role_name FROM users u JOIN roles r ON u.role_id = r.id";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("role_id"),
                    rs.getString("role_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public java.util.List<String> getAllRoleNames() {
        java.util.List<String> roles = new java.util.ArrayList<>();
        String sql = "SELECT name FROM roles";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                roles.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public boolean addUser(String username, String password, String roleName) {
        String sql = "INSERT INTO users (username, password, role_id) VALUES (?, ?, (SELECT id FROM roles WHERE name = ?))";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, roleName);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int id, String username, String password, String roleName) {
        String sql = "UPDATE users SET username = ?, password = ?, role_id = (SELECT id FROM roles WHERE name = ?) WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, roleName);
            stmt.setInt(4, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(int userId, String newUsername, String newPassword) {
        String sql;
        if (newPassword != null && !newPassword.isEmpty()) {
            // Обновляем и имя пользователя, и пароль
            sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";
        } else {
            // Обновляем только имя пользователя
            sql = "UPDATE users SET username = ? WHERE id = ?";
        }
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newUsername);
            if (newPassword != null && !newPassword.isEmpty()) {
                stmt.setString(2, newPassword);
                stmt.setInt(3, userId);
            } else {
                stmt.setInt(2, userId);
            }
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 