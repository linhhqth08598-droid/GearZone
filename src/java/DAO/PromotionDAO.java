package DAO;

import Models.Promotion;
import Url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {

    // 1. Lấy toàn bộ danh sách Khuyến Mãi (Dành cho Admin và trang danh sách)
    public List<Promotion> getAllPromotions() {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT * FROM Promotions ORDER BY PromotionID DESC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Promotion promo = new Promotion();
                promo.setPromotionId(rs.getInt("PromotionID"));
                promo.setPromoCode(rs.getString("PromoCode"));
                promo.setPromotionName(rs.getString("PromotionName"));
                promo.setDiscountPercent(rs.getDouble("DiscountPercent"));
                promo.setDiscountAmount(rs.getDouble("DiscountAmount"));
                promo.setMinOrderValue(rs.getDouble("MinOrderValue"));
                promo.setStartDate(rs.getDate("StartDate"));
                promo.setEndDate(rs.getDate("EndDate"));
                promo.setUsageLimit(rs.getInt("UsageLimit"));
                promo.setUsedCount(rs.getInt("UsedCount"));
                promo.setIsActive(rs.getBoolean("IsActive"));
                list.add(promo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Hàm đã có: Lấy mã giảm giá hợp lệ
    public Promotion getValidPromotion(String promoCode) {
        Promotion promo = null;
        String sql = "SELECT * FROM Promotions WHERE PromoCode = ? AND IsActive = 1 " +
                     "AND GETDATE() BETWEEN StartDate AND EndDate " +
                     "AND (UsageLimit IS NULL OR UsedCount < UsageLimit)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, promoCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                promo = new Promotion();
                promo.setPromotionId(rs.getInt("PromotionID"));
                promo.setPromoCode(rs.getString("PromoCode"));
                promo.setPromotionName(rs.getString("PromotionName"));
                promo.setDiscountPercent(rs.getDouble("DiscountPercent"));
                promo.setDiscountAmount(rs.getDouble("DiscountAmount"));
                promo.setMinOrderValue(rs.getDouble("MinOrderValue"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return promo;
    }

    // 3. Thêm mới Khuyến Mãi (Admin)
    public void insertPromotion(String code, String name, double percent, double amount, double minOrder, String startDate, String endDate, int limit, boolean isActive) {
        String sql = "INSERT INTO Promotions (PromoCode, PromotionName, DiscountPercent, DiscountAmount, MinOrderValue, StartDate, EndDate, UsageLimit, UsedCount, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code.toUpperCase());
            ps.setString(2, name);
            ps.setDouble(3, percent);
            ps.setDouble(4, amount);
            ps.setDouble(5, minOrder);
            ps.setString(6, startDate);
            ps.setString(7, endDate);
            ps.setInt(8, limit);
            ps.setBoolean(9, isActive);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Cập nhật Khuyến Mãi (Admin)
    public void updatePromotion(int id, String code, String name, double percent, double amount, double minOrder, String startDate, String endDate, int limit, boolean isActive) {
        String sql = "UPDATE Promotions SET PromoCode=?, PromotionName=?, DiscountPercent=?, DiscountAmount=?, MinOrderValue=?, StartDate=?, EndDate=?, UsageLimit=?, IsActive=? WHERE PromotionID=?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code.toUpperCase());
            ps.setString(2, name);
            ps.setDouble(3, percent);
            ps.setDouble(4, amount);
            ps.setDouble(5, minOrder);
            ps.setString(6, startDate);
            ps.setString(7, endDate);
            ps.setInt(8, limit);
            ps.setBoolean(9, isActive);
            ps.setInt(10, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 5. Xóa Khuyến Mãi (Admin)
    public void deletePromotion(int id) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Promotions WHERE PromotionID = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}