package DAO;

import Models.AssemblyOrders;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssemblyOrdersDAO {

    public List<AssemblyOrders> getAll() {
        List<AssemblyOrders> list = new ArrayList<>();
        String sql = "SELECT * FROM [assembly_orders]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssemblyOrders ao = new AssemblyOrders();
                    ao.setAssemblyId(rs.getInt("assembly_id"));
                    ao.setUserId(rs.getInt("user_id"));
                    ao.setOrderId(rs.getInt("order_id"));
                    ao.setBuildId(rs.getInt("build_id"));
                    ao.setTechnicianId(rs.getInt("technician_id"));
                    ao.setAssemblyType(rs.getString("assembly_type"));
                    ao.setAssemblyFee(rs.getDouble("assembly_fee"));
                    ao.setStatus(rs.getString("status"));
                    ao.setScheduledDate(rs.getDate("scheduled_date"));
                    ao.setHandedOverAt(rs.getDate("handed_over_at"));
                    ao.setCustomerRating(rs.getInt("customer_rating"));
                    ao.setCustomerFeedback(rs.getString("customer_feedback"));
                    ao.setNote(rs.getString("note"));
                    ao.setCreatedAt(rs.getDate("created_at"));
                    list.add(ao);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
