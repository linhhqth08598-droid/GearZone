package dungntth08581.dao;

import model.OrderItems;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemsDAO {

    public List<OrderItems> getAll() {
        List<OrderItems> list = new ArrayList<>();
        String sql = "SELECT * FROM [order_items]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrderItems obj = new OrderItems();
                obj.setOrderItemId(rs.getInt("order_item_id"));
                obj.setOrderId(rs.getInt("order_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setSerialNumber(rs.getString("serial_number"));
                obj.setQuantity(rs.getInt("quantity"));
                obj.setUnitPrice(rs.getDouble("unit_price"));
                obj.setCostPrice(rs.getDouble("cost_price"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
