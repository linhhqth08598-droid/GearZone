package linhhqth098.service;

import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ReportService {

    public double getTotalRevenue() {
        double total = 0;
        String sql = "SELECT SUM(final_amount) FROM orders WHERE order_status != 'CANCELLED'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    public int getTotalOrders() {
        int total = 0;
        String sql = "SELECT COUNT(order_id) FROM orders";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    public int getTotalPartsSold() {
        int total = 0;
        String sql = "SELECT SUM(oi.quantity) FROM order_items oi JOIN orders o ON oi.order_id = o.order_id WHERE o.order_status != 'CANCELLED'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    // Lấy doanh thu theo từng danh mục (Dùng cho biểu đồ tròn)
    public Map<String, Double> getRevenueByCategory() {
        Map<String, Double> map = new HashMap<>();
        String sql = "SELECT c.category_name, SUM(oi.unit_price * oi.quantity) AS revenue " +
                     "FROM order_items oi JOIN parts p ON oi.part_id = p.part_id " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "JOIN orders o ON oi.order_id = o.order_id " +
                     "WHERE o.order_status != 'CANCELLED' GROUP BY c.category_name";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("category_name"), rs.getDouble("revenue"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }
    
    // Lấy Top 5 linh kiện bán chạy
    public Map<String, Integer> getTopSellingParts() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT TOP 5 p.part_name, SUM(oi.quantity) AS qty " +
                     "FROM order_items oi JOIN parts p ON oi.part_id = p.part_id " +
                     "JOIN orders o ON oi.order_id = o.order_id " +
                     "WHERE o.order_status != 'CANCELLED' GROUP BY p.part_name ORDER BY qty DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("part_name"), rs.getInt("qty"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }
}