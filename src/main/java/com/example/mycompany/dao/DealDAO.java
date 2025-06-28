package com.example.mycompany.dao;

import com.example.mycompany.model.Deal;
import com.example.mycompany.model.DealProduct;
import com.example.mycompany.model.Discount;
import com.example.mycompany.Database;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DealDAO {
    private final DealProductDAO dealProductDAO = new DealProductDAO();

    public List<Deal> getAllDeals(boolean withProducts) {
        List<Deal> deals = new ArrayList<>();
        String sql = "SELECT d.id, c.name AS customer_name, d.deal_date, d.deal_type, d.discount_id " +
                "FROM deals d JOIN customers c ON d.customer_id = c.id ORDER BY d.deal_date DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // Загружаем все скидки один раз
            DiscountDAO discountDAO = new DiscountDAO();
            List<Discount> allDiscounts = discountDAO.getAllDiscountsForDeals();
            Map<Integer, Discount> discountCache = new HashMap<>();
            for (Discount discount : allDiscounts) {
                discountCache.put(discount.getId(), discount);
            }
            
            // Загружаем все продукты сделок одним запросом
            Map<Integer, List<DealProduct>> allDealProducts = withProducts ? dealProductDAO.getAllDealProducts() : new HashMap<>();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String customerName = rs.getString("customer_name");
                LocalDate date = rs.getDate("deal_date").toLocalDate();
                String type = rs.getString("deal_type");
                Integer discountId = rs.getObject("discount_id") != null ? rs.getInt("discount_id") : null;
                
                double discountPercent = 0.0;
                if (discountId != null) {
                    Discount discount = discountCache.get(discountId);
                    if (discount != null) discountPercent = discount.getPercent() / 100.0;
                }
                
                List<DealProduct> products = withProducts ? allDealProducts.get(id) : null;
                if (products == null) products = new ArrayList<>();
                
                double total = 0.0;
                if (products != null && !products.isEmpty()) {
                    total = products.stream().mapToDouble(dp -> dp.getPrice() * dp.getQuantity()).sum();
                    total = total * (1 - discountPercent);
                }
                deals.add(new Deal(id, customerName, date, type, discountPercent, total, products));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deals;
    }

    public Deal getDealById(int dealId) {
        String sql = "SELECT d.id, c.name AS customer_name, d.deal_date, d.deal_type, d.discount_id " +
                "FROM deals d JOIN customers c ON d.customer_id = c.id WHERE d.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dealId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String customerName = rs.getString("customer_name");
                    LocalDate date = rs.getDate("deal_date").toLocalDate();
                    String type = rs.getString("deal_type");
                    Integer discountId = rs.getObject("discount_id") != null ? rs.getInt("discount_id") : null;
                    double discountPercent = 0.0;
                    if (discountId != null) {
                        DiscountDAO discountDAO = new DiscountDAO();
                        Discount discount = discountDAO.getById(discountId);
                        if (discount != null) discountPercent = discount.getPercent() / 100.0;
                    }
                    List<DealProduct> products = dealProductDAO.getDealProducts(id);
                    double total = 0.0;
                    if (products != null) {
                        total = products.stream().mapToDouble(dp -> dp.getPrice() * dp.getQuantity()).sum();
                        total = total * (1 - discountPercent);
                    }
                    return new Deal(id, customerName, date, type, discountPercent, total, products);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addDeal(Deal deal, int customerId) {
        double total = deal.getProducts().stream().mapToDouble(dp -> dp.getPrice() * dp.getQuantity()).sum();
        int amount = deal.getProducts().stream().mapToInt(dp -> dp.getQuantity()).sum();
        
        // Загружаем все скидки один раз и находим лучшую
        DiscountDAO discountDAO = new DiscountDAO();
        List<Discount> allDiscounts = discountDAO.getAllDiscountsForDeals();
        Discount bestDiscount = null;
        for (Discount discount : allDiscounts) {
            if (discount.getMinTotalCost() <= total && discount.getMinTotalAmount() <= amount) {
                if (bestDiscount == null || discount.getPercent() > bestDiscount.getPercent()) {
                    bestDiscount = discount;
                }
            }
        }
        
        Integer discountId = (bestDiscount != null) ? bestDiscount.getId() : null;
        double discountPercent = (bestDiscount != null) ? bestDiscount.getPercent() / 100.0 : 0.0;
        double totalWithDiscount = total * (1 - discountPercent);
        
        String sql = "INSERT INTO deals (customer_id, deal_date, deal_type, discount_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setDate(2, java.sql.Date.valueOf(deal.getDate()));
            stmt.setString(3, deal.getType());
            if (discountId != null) stmt.setInt(4, discountId); else stmt.setNull(4, java.sql.Types.INTEGER);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return -1;
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int dealId = generatedKeys.getInt(1);
                    if (deal.getProducts() != null && !deal.getProducts().isEmpty()) {
                        dealProductDAO.addDealProducts(dealId, deal.getProducts());
                    }
                    // Обновляем deal для UI
                    deal.setDiscount(discountPercent);
                    deal.setTotal(totalWithDiscount);
                    return dealId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateDeal(Deal deal, int customerId) {
        double total = deal.getProducts().stream().mapToDouble(dp -> dp.getPrice() * dp.getQuantity()).sum();
        int amount = deal.getProducts().stream().mapToInt(dp -> dp.getQuantity()).sum();
        
        // Загружаем все скидки один раз и находим лучшую
        DiscountDAO discountDAO = new DiscountDAO();
        List<Discount> allDiscounts = discountDAO.getAllDiscountsForDeals();
        
        Discount bestDiscount = null;
        for (Discount discount : allDiscounts) {
            if (discount.getMinTotalCost() <= total && discount.getMinTotalAmount() <= amount) {
                if (bestDiscount == null || discount.getPercent() > bestDiscount.getPercent()) {
                    bestDiscount = discount;
                }
            }
        }
        
        Integer discountId = (bestDiscount != null) ? bestDiscount.getId() : null;
        double discountPercent = (bestDiscount != null) ? bestDiscount.getPercent() / 100.0 : 0.0;
        double totalWithDiscount = total * (1 - discountPercent);
        
        String sql = "UPDATE deals SET customer_id=?, deal_date=?, deal_type=?, discount_id=? WHERE id=?";
        
        try (Connection conn = Database.getConnection()) {
            
            // Обновляем основную информацию о сделке
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                stmt.setDate(2, java.sql.Date.valueOf(deal.getDate()));
                stmt.setString(3, deal.getType());
                if (discountId != null) stmt.setInt(4, discountId); else stmt.setNull(4, java.sql.Types.INTEGER);
                stmt.setInt(5, deal.getId());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    return false;
                }
            }
            
            // Обновляем продукты сделки
            if (!dealProductDAO.updateDealProducts(deal.getId(), deal.getProducts())) {
                return false;
            }
            
            // Обновляем deal для UI
            deal.setDiscount(discountPercent);
            deal.setTotal(totalWithDiscount);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDeal(int dealId) {
        String sql = "DELETE FROM deals WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dealId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 