package com.example.mycompany.dao;

import com.example.mycompany.model.DealProduct;
import com.example.mycompany.model.Product;
import com.example.mycompany.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DealProductDAO {
    public List<DealProduct> getDealProducts(int dealId) {
        List<DealProduct> products = new ArrayList<>();
        String sql = "SELECT dp.product_id, dp.amount, dp.product_price, p.name, p.wholesale_price, p.retail_price, p.description " +
                "FROM deal_products dp JOIN products p ON dp.product_id = p.id WHERE dp.deal_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dealId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    String name = rs.getString("name");
                    double wholesalePrice = rs.getDouble("wholesale_price");
                    double retailPrice = rs.getDouble("retail_price");
                    String description = rs.getString("description");
                    int quantity = rs.getInt("amount");
                    double price = rs.getDouble("product_price");
                    double sum = price * quantity;
                    Product product = new Product(productId, name, wholesalePrice, retailPrice, description);
                    products.add(new DealProduct(product, quantity, price, sum));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Map<Integer, List<DealProduct>> getAllDealProducts() {
        Map<Integer, List<DealProduct>> dealProductsMap = new HashMap<>();
        String sql = "SELECT dp.deal_id, dp.product_id, dp.amount, dp.product_price, p.name, p.wholesale_price, p.retail_price, p.description " +
                "FROM deal_products dp JOIN products p ON dp.product_id = p.id ORDER BY dp.deal_id";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int dealId = rs.getInt("deal_id");
                int productId = rs.getInt("product_id");
                String name = rs.getString("name");
                double wholesalePrice = rs.getDouble("wholesale_price");
                double retailPrice = rs.getDouble("retail_price");
                String description = rs.getString("description");
                int quantity = rs.getInt("amount");
                double price = rs.getDouble("product_price");
                double sum = price * quantity;
                Product product = new Product(productId, name, wholesalePrice, retailPrice, description);
                DealProduct dealProduct = new DealProduct(product, quantity, price, sum);
                
                dealProductsMap.computeIfAbsent(dealId, k -> new ArrayList<>()).add(dealProduct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dealProductsMap;
    }

    public boolean addDealProducts(int dealId, List<DealProduct> dealProducts) {
        String sql = "INSERT INTO deal_products (deal_id, product_id, amount, product_price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < dealProducts.size(); i++) {
                DealProduct dp = dealProducts.get(i);
                
                stmt.setInt(1, dealId);
                stmt.setInt(2, dp.getProduct().getId());
                stmt.setInt(3, dp.getQuantity());
                stmt.setDouble(4, dp.getPrice());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    return false;
                }
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDealProducts(int dealId) {
        String sql = "DELETE FROM deal_products WHERE deal_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dealId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDealProducts(int dealId, List<DealProduct> newDealProducts) {
        try (Connection conn = Database.getConnection()) {
            
            // Удаляем все старые продукты
            String deleteSql = "DELETE FROM deal_products WHERE deal_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, dealId);
                deleteStmt.executeUpdate();
            }
            
            // Проверка на дубликаты product_id
            if (newDealProducts != null && !newDealProducts.isEmpty()) {
                Set<Integer> productIds = new HashSet<>();
                for (DealProduct dp : newDealProducts) {
                    if (!productIds.add(dp.getProduct().getId())) {
                        return false;
                    }
                }
            }
            
            // Добавляем новые продукты
            if (newDealProducts != null && !newDealProducts.isEmpty()) {
                String insertSql = "INSERT INTO deal_products (deal_id, product_id, amount, product_price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    for (int i = 0; i < newDealProducts.size(); i++) {
                        DealProduct dp = newDealProducts.get(i);
                        
                        insertStmt.setInt(1, dealId);
                        insertStmt.setInt(2, dp.getProduct().getId());
                        insertStmt.setInt(3, dp.getQuantity());
                        insertStmt.setDouble(4, dp.getPrice());
                        
                        int affectedRows = insertStmt.executeUpdate();
                        
                        if (affectedRows == 0) {
                            return false;
                        }
                    }
                }
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 