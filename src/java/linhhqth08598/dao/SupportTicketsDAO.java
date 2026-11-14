package linhhqth08598.dao;

import fourSome.model.SupportTickets;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupportTicketsDAO {

    public List<SupportTickets> getAll() {
        List<SupportTickets> list = new ArrayList<>();
        String sql = "SELECT * FROM [support_tickets]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SupportTickets obj = new SupportTickets();
                obj.setTicketId(rs.getInt("ticket_id"));
                obj.setTicketCode(rs.getString("ticket_code"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setTicketType(rs.getString("ticket_type"));
                obj.setSubject(rs.getString("subject"));
                obj.setContent(rs.getString("content"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setOrderId(rs.getInt("order_id"));
                obj.setBuildId(rs.getInt("build_id"));
                obj.setCurrentPcId(rs.getInt("current_pc_id"));
                obj.setRequirementId(rs.getInt("requirement_id"));
                obj.setAssignedStaffId(rs.getInt("assigned_staff_id"));
                obj.setPriority(rs.getString("priority"));
                obj.setStatus(rs.getString("status"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setResolvedAt(rs.getDate("resolved_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
        // 1. Tạo ticket mới
    public boolean insertTicket(SupportTickets ticket) {
        String sql = "INSERT INTO support_tickets (ticket_code, user_id, ticket_type, subject, content, priority, status, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, 'NORMAL', 'OPEN', GETDATE())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticket.getTicketCode());
            ps.setInt(2, ticket.getUserId());
            ps.setString(3, ticket.getTicketType());
            ps.setString(4, ticket.getSubject());
            ps.setString(5, ticket.getContent());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Lấy danh sách ticket của user
    public List<SupportTickets> getByUserId(int userId) {
        List<SupportTickets> list = new ArrayList<>();
        String sql = "SELECT * FROM support_tickets WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupportTickets t = new SupportTickets();
                    t.setTicketId(rs.getInt("ticket_id"));
                    t.setTicketCode(rs.getString("ticket_code"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setTicketType(rs.getString("ticket_type"));
                    t.setSubject(rs.getString("subject"));
                    t.setContent(rs.getString("content"));
                    t.setPriority(rs.getString("priority"));
                    t.setStatus(rs.getString("status"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
        // Admin: Lấy tất cả ticket kèm tên user
    public List<SupportTickets> getAllTicketsForAdmin() {
        List<SupportTickets> list = new ArrayList<>();
        String sql = "SELECT t.*, u.full_name, u.email "
                   + "FROM support_tickets t "
                   + "JOIN users u ON t.user_id = u.user_id "
                   + "ORDER BY t.created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SupportTickets t = new SupportTickets();
                t.setTicketId(rs.getInt("ticket_id"));
                t.setTicketCode(rs.getString("ticket_code"));
                t.setUserId(rs.getInt("user_id"));
                t.setTicketType(rs.getString("ticket_type"));
                t.setSubject(rs.getString("subject"));
                t.setContent(rs.getString("content"));
                t.setPriority(rs.getString("priority"));
                t.setStatus(rs.getString("status"));
                t.setCreatedAt(rs.getTimestamp("created_at"));
                // Các trường join (nếu model có)
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Admin: Cập nhật trạng thái ticket (Đang xử lý, Đã giải quyết, Đóng)
    public boolean updateTicketStatus(int ticketId, String status) {
        String sql = "UPDATE support_tickets SET status = ? WHERE ticket_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
