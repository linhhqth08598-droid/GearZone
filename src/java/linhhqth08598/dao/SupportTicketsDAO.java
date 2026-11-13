package linhhqth08598.dao;

import model.SupportTickets;
import url.DBConnect;
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
}
