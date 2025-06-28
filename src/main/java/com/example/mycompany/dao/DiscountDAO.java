package com.example.mycompany.dao;

import com.example.mycompany.model.Discount;
import com.example.mycompany.Database;
import com.example.mycompany.model.User;

import java.sql.*;
import java.util.List;

public class DiscountDAO {
    public Discount getById(int id) {
        String sql = "SELECT * FROM discounts WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Discount(
                        rs.getInt("id"),
                        rs.getDouble("percent"),
                        rs.getDouble("min_total_cost"),
                        rs.getInt("min_total_amount")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Discount> getAllDiscounts(User currentUser) {
        if (!"admin".equals(currentUser.getRoleName())) {
            throw new SecurityException("Нет доступа");
        }
        java.util.List<com.example.mycompany.model.Discount> discounts = new java.util.ArrayList<>();
        String sql = "SELECT * FROM discounts";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                discounts.add(new com.example.mycompany.model.Discount(
                    rs.getInt("id"),
                    rs.getDouble("percent"),
                    rs.getDouble("min_total_cost"),
                    rs.getInt("min_total_amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    public List<Discount> getAllDiscountsForDeals() {
        java.util.List<Discount> discounts = new java.util.ArrayList<>();
        String sql = "SELECT * FROM discounts";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                discounts.add(new com.example.mycompany.model.Discount(
                    rs.getInt("id"),
                    rs.getDouble("percent"),
                    rs.getDouble("min_total_cost"),
                    rs.getInt("min_total_amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    public boolean addDiscount(double percent, double minTotalCost, int minTotalAmount) {
        String sql = "INSERT INTO discounts (percent, min_total_cost, min_total_amount) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, percent);
            stmt.setDouble(2, minTotalCost);
            stmt.setInt(3, minTotalAmount);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDiscount(int id, double percent, double minTotalCost, int minTotalAmount) {
        String sql = "UPDATE discounts SET percent = ?, min_total_cost = ?, min_total_amount = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, percent);
            stmt.setDouble(2, minTotalCost);
            stmt.setInt(3, minTotalAmount);
            stmt.setInt(4, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDiscount(int id) {
        String sql = "DELETE FROM discounts WHERE id = ?";
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
} 