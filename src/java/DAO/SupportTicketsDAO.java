package DAO;

import Models.SupportTickets;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupportTicketsDAO {

    public List<SupportTickets> getAll() {
        List<SupportTickets> list = new ArrayList<>();
        String sql = "SELECT * FROM [support_tickets]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupportTickets st = new SupportTickets();
                    st.setTicketId(rs.getInt("ticket_id"));
                    st.setTicketCode(rs.getString("ticket_code"));
                    st.setUserId(rs.getInt("user_id"));
                    st.setTicketType(rs.getString("ticket_type"));
                    st.setSubject(rs.getString("subject"));
                    st.setContent(rs.getString("content"));
                    st.setPartId(rs.getInt("part_id"));
                    st.setOrderId(rs.getInt("order_id"));
                    st.setBuildId(rs.getInt("build_id"));
                    st.setCurrentPcId(rs.getInt("current_pc_id"));
                    st.setRequirementId(rs.getInt("requirement_id"));
                    st.setAssignedStaffId(rs.getInt("assigned_staff_id"));
                    st.setPriority(rs.getString("priority"));
                    st.setStatus(rs.getString("status"));
                    st.setCreatedAt(rs.getDate("created_at"));
                    st.setResolvedAt(rs.getDate("resolved_at"));
                    list.add(st);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
