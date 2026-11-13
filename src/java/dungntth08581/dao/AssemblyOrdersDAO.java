package dungntth08581.dao;

import model.AssemblyOrders;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssemblyOrdersDAO {

    public List<AssemblyOrders> getAll() {
        List<AssemblyOrders> list = new ArrayList<>();
        String sql = "SELECT * FROM [assembly_orders]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AssemblyOrders obj = new AssemblyOrders();
                obj.setAssemblyId(rs.getInt("assembly_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setOrderId(rs.getInt("order_id"));
                obj.setBuildId(rs.getInt("build_id"));
                obj.setTechnicianId(rs.getInt("technician_id"));
                obj.setAssemblyType(rs.getString("assembly_type"));
                obj.setAssemblyFee(rs.getDouble("assembly_fee"));
                obj.setStatus(rs.getString("status"));
                obj.setScheduledDate(rs.getDate("scheduled_date"));
                obj.setHandedOverAt(rs.getDate("handed_over_at"));
                obj.setCustomerRating(rs.getInt("customer_rating"));
                obj.setCustomerFeedback(rs.getString("customer_feedback"));
                obj.setNote(rs.getString("note"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
